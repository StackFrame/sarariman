/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.logincookies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LoginCookies {

    private final DataSource dataSource;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Runnable cleanupTask = new Runnable() {
        @Override
        public void run() {
            // FIXME: Put this on a SQL write background task.
            try {
                Connection c = dataSource.getConnection();
                try {
                    PreparedStatement s = c.prepareStatement("DELETE FROM login_cookie " +
                            "WHERE last_used < DATE_SUB(NOW(), INTERVAL ? SECOND)");
                    try {
                        s.setInt(1, maxAge);
                        s.execute();
                    } finally {
                        s.close();
                    }
                } finally {
                    c.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "exception thrown while trying to delete expired cookies", e);
            }
        }

    };

    public LoginCookies(DataSource dataSource, ScheduledThreadPoolExecutor timer) {
        this.dataSource = dataSource;
        timer.scheduleAtFixedRate(cleanupTask, TimeUnit.MINUTES.toMillis(1), TimeUnit.HOURS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    private static final int maxAge = (int)TimeUnit.DAYS.toSeconds(7);

    private static final String cookieName = "login";

    private LoginCookie create(String username, String userAgent, String remoteAddress) throws SQLException {
        UUID token = UUID.randomUUID();
        Connection c = dataSource.getConnection();
        try {
            Timestamp now = new Timestamp(new Date().getTime());
            PreparedStatement s = c.prepareStatement(
                    "INSERT INTO login_cookie(username, token, created, last_used, user_agent, remote_address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            try {
                s.setString(1, username);
                s.setString(2, token.toString());
                s.setTimestamp(3, now);
                s.setTimestamp(4, now);
                s.setString(5, userAgent);
                s.setString(6, remoteAddress);
                int numRowsInserted = s.executeUpdate();
                assert numRowsInserted == 1;
                ResultSet keys = s.getGeneratedKeys();
                try {
                    keys.next();
                    int key = keys.getInt(1);
                    return new LoginCookie(key, dataSource);
                } finally {
                    keys.close();
                }
            } finally {
                s.close();
            }
        } finally {
            c.close();
        }
    }

    public Cookie storeLoginToken(String username, HttpServletRequest request) throws SQLException {
        String userAgent = request.getHeader("User-Agent");
        String remoteAddress = request.getRemoteAddr();
        LoginCookie loginCookie = create(username, userAgent, remoteAddress);
        Cookie cookie = new Cookie(cookieName, loginCookie.getToken().toString());
        cookie.setMaxAge(maxAge);
        String path = request.getContextPath();
        if (path.isEmpty()) {
            path = "/";
        }

        cookie.setPath(path);

        // Enable this when we step up to a container that supports the Servlet 3.0 specification.
        // cookie.setHttpOnly(true);

        cookie.setSecure(true);
        return cookie;
    }

    private Cookie getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public LoginCookie findLoginCookie(HttpServletRequest request) {
        Cookie cookie = getCookie(request);
        if (cookie == null) {
            return null;
        }

        return get(UUID.fromString(cookie.getValue()));
    }

    public Cookie makeDeleteCookie() {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        return cookie;
    }

    private LoginCookie get(UUID token) {
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement("SELECT id FROM login_cookie WHERE token = ?");
                s.setString(1, token.toString());
                ResultSet r = s.executeQuery();
                try {
                    if (r.first()) {
                        int id = r.getInt("id");
                        return new LoginCookie(id, dataSource);
                    } else {
                        return null;
                    }
                } finally {
                    r.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
