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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class StatusChangeHandler extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        dataSource = ((Sarariman)getServletContext().getAttribute("sarariman")).getDataSource();
    }

    private void updateStatus(int ticket, String status, int employee) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO ticket_status (ticket, status, employee) VALUES(?, ?, ?)");
            try {
                ps.setInt(1, ticket);
                ps.setString(2, status);
                ps.setInt(3, employee);
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
        int ticket = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");
        Employee employee = (Employee)request.getAttribute("user");
        try {
            updateStatus(ticket, status, employee.getNumber());
            Ticket ticketBean = new TicketImpl(ticket, dataSource);
            Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
            URL ticketURL = sarariman.getTicketURL(ticketBean);
            String messageBody = String.format("%s changed the status of ticket %d to %s.\n\nGo to %s to view.", employee.getDisplayName(), ticket, status, ticketURL);
            String messageSubject = String.format("ticket %d: new status: %s", ticket, status);
            sarariman.getEmailDispatcher().send(EmailDispatcher.addresses(ticketBean.getStakeholders()), null, messageSubject, messageBody);

            // FIXME: If external_creator_email is set, send to it.

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
        return "handles changes in status of a ticket";
    }

}
