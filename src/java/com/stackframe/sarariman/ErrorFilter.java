/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Throwables;
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
public class ErrorFilter extends HttpFilter {

    private DataSource dataSource;

    private Executor executor;

    public void init(FilterConfig filterConfig) throws ServletException {
        Sarariman sarariman = (Sarariman)filterConfig.getServletContext().getAttribute("sarariman");
        dataSource = sarariman.getDataSource();
        executor = sarariman.getBackgroundDatabaseWriteExecutor();
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (final Exception e) {
            e.printStackTrace();
            final String stackTrace = Throwables.getStackTraceAsString(e);
            request.setAttribute("stacktrace", stackTrace);
            if (!response.isCommitted()) {
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

            // One would think that the HttpServletRequest could just be marked final and used in the Runnable, but it gets reused after
            // this call.
            final String path = request.getServletPath();
            final String queryString = request.getQueryString();
            final String method = request.getMethod();
            final String userAgent = request.getHeader("User-Agent");
            final String remoteAddress = request.getRemoteAddr();
            final String referrer = request.getHeader("Referer");

            final Employee employee = (Employee)request.getAttribute("user");
            Runnable insertTask = new Runnable() {
                public void run() {
                    try {
                        Connection c = dataSource.getConnection();
                        try {
                            PreparedStatement s = c.prepareStatement(
                                    "INSERT INTO error_log (path, query, method, employee, user_agent, remote_address, exception, referrer) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
                            try {
                                s.setString(1, path);
                                s.setString(2, queryString);
                                s.setString(3, method);
                                if (employee == null) {
                                    s.setObject(4, null);
                                } else {
                                    s.setInt(4, employee.getNumber());
                                }

                                s.setString(5, userAgent);
                                s.setString(6, remoteAddress);
                                s.setString(7, stackTrace);
                                s.setString(8, referrer);
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
    }

    public void destroy() {
    }

}
