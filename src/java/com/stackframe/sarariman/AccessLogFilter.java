/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class AccessLogFilter extends HttpFilter {

    private DataSource dataSource;

    private Executor executor;

    public void init(FilterConfig filterConfig) throws ServletException {
        Sarariman sarariman = (Sarariman)filterConfig.getServletContext().getAttribute("sarariman");
        dataSource = sarariman.getDataSource();
        executor = sarariman.getBackgroundDatabaseWriteExecutor();
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // FIXME: This is a hack because the deployed version of Tomcat doesn't support getStatus(). Ditch it when we upgrade.
        final StatusExposingServletResponse sesr = new StatusExposingServletResponse(response);

        long start = System.currentTimeMillis();
        chain.doFilter(request, sesr);
        long stop = System.currentTimeMillis();
        final long took = stop - start;

        // One would think that the HttpServletRequest could just be marked final and used in the Runnable, but it gets reused after
        // this call.
        final String path = request.getServletPath();
        final String queryString = request.getQueryString();
        final String method = request.getMethod();
        final String userAgent = request.getHeader("User-Agent");
        final String remoteAddress = request.getRemoteAddr();
        final String referrer = request.getHeader("Referer");
        final String host = request.getHeader("Host");

        final Employee employee = (Employee)request.getAttribute("user");
        Runnable insertTask = new Runnable() {
            public void run() {
                try {
                    Connection c = dataSource.getConnection();
                    try {
                        PreparedStatement s = c.prepareStatement(
                                "INSERT INTO access_log (path, query, method, employee, status, time, user_agent, remote_address, referrer, host) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        try {
                            s.setString(1, path);
                            s.setString(2, queryString);
                            s.setString(3, method);
                            if (employee == null) {
                                s.setObject(4, null);
                            } else {
                                s.setInt(4, employee.getNumber());
                            }

                            // We will always get 0 if no error is set, because of the aforementioned Tomcat/Servlet spec. issue.
                            int status = sesr.getStatus();
                            if (status == 0) {
                                status = 200;
                            }

                            s.setInt(5, status);
                            s.setLong(6, took);
                            s.setString(7, userAgent);
                            s.setString(8, remoteAddress);
                            s.setString(9, referrer);
                            s.setString(10, host);
                            int numRowsInserted = s.executeUpdate();
                            assert numRowsInserted == 1;
                        } finally {
                            s.close();
                        }
                    } finally {
                        c.close();
                    }
                } catch (SQLException e) {
                    // FIXME: Should we log this exception? Does it kill the Executor?
                    throw new RuntimeException(e);
                }
            }

        };
        executor.execute(insertTask);
    }

    public void destroy() {
    }

}
