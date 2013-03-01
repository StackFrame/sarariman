/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.EmailDispatcher;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Sarariman;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TextUpdateHandler extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        dataSource = ((Sarariman)getServletContext().getAttribute("sarariman")).getDataSource();
    }

    private void update(int ticket, String table, String text, int updater) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("INSERT INTO ticket_%s (ticket, %s, employee) VALUES(?, ?, ?)", table, table));
            try {
                ps.setInt(1, ticket);
                ps.setString(2, text);
                ps.setInt(3, updater);
                ps.execute();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    private void sendNameChangeEmail(int ticket, Sarariman sarariman, Employee updater, String name, URL viewURL, Iterable<InternetAddress> to) {
        String messageSubject = String.format("ticket %d name changed to \"%s\"", ticket, name);
        String messageBody = String.format("%s changed the name of ticket %d to \"%s\".\n\nGo to %s to view.", updater.getDisplayName(), ticket, name, viewURL);
        sarariman.getEmailDispatcher().send(to, null, messageSubject, messageBody);
    }

    private void sendDescriptionChangeEmail(int ticket, Sarariman sarariman, Employee updater, String description, URL viewURL, Iterable<InternetAddress> to) {
        String messageSubject = String.format("ticket %d: description changed", ticket);
        String messageBody = String.format("%s changed the description of ticket %d (%s) to:\n\n%s", updater.getDisplayName(), ticket, viewURL, description);
        sarariman.getEmailDispatcher().send(to, null, messageSubject, messageBody);
    }

    private void sendCommentEmail(int ticket, Sarariman sarariman, Employee updater, String comment, URL viewURL, Iterable<InternetAddress> to) {
        String messageSubject = String.format("ticket %d: commented", ticket);
        String messageBody = String.format("%s commented on ticket %d (%s):\n\n%s", updater.getDisplayName(), ticket, viewURL, comment);
        sarariman.getEmailDispatcher().send(to, null, messageSubject, messageBody);
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
        String table = request.getParameter("table");
        String text = request.getParameter("text");
        Employee updater = (Employee)request.getAttribute("user");
        try {
            // FIXME: Check table name before update to defend against SQL injection attack.
            update(ticket, table, text, updater.getNumber());
            Ticket ticketBean = new TicketImpl(ticket, dataSource);
            Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
            URL viewTicketURL = sarariman.getTicketURL(ticketBean);
            if (table.equals("description")) {
                sendDescriptionChangeEmail(ticket, sarariman, updater, text, viewTicketURL, EmailDispatcher.addresses(ticketBean.getStakeholders()));
            } else if (table.equals("name")) {
                sendNameChangeEmail(ticket, sarariman, updater, text, viewTicketURL, EmailDispatcher.addresses(ticketBean.getStakeholders()));
            } else if (table.equals("comment")) {
                sendCommentEmail(ticket, sarariman, updater, text, viewTicketURL, EmailDispatcher.addresses(ticketBean.getStakeholders()));
            } else {
                throw new IllegalArgumentException("invalid table: " + table);
            }

            response.sendRedirect(request.getHeader("Referer"));
        } catch (NoSuchTicketException nste) {
            throw new ServletException(nste);
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
        return "handles updates for text associated with a ticket";
    }

}
