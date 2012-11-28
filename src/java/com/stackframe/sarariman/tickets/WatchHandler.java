/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class WatchHandler extends HttpServlet {

    private Connection openConnection() throws SQLException {
        try {
            DataSource source = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
            return source.getConnection();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void watch(int ticket, int watcher, boolean watch) throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement ps;
            if (watch) {
                ps = connection.prepareStatement("INSERT INTO ticket_watcher (ticket, employee) VALUES(?, ?)");
            } else {
                ps = connection.prepareStatement("DELETE FROM ticket_watcher WHERE ticket = ? AND employee = ?");
            }

            try {
                ps.setInt(1, ticket);
                ps.setInt(2, watcher);
                ps.execute();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ticket = Integer.parseInt(request.getParameter("ticket"));
        int watcher = Integer.parseInt(request.getParameter("watcher"));
        boolean watch = Boolean.parseBoolean(request.getParameter("watch"));
        try {
            watch(ticket, watcher,watch);
            response.sendRedirect(request.getHeader("Referer"));
        } catch (SQLException se) {
            throw new ServletException(se);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "handles changes in watch status of a ticket";
    }

}
