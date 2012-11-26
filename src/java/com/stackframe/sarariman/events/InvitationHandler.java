/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.events;

import com.stackframe.sarariman.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class InvitationHandler extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private void logInvitation(int event, int employee) throws SQLException {
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO company_events_invitation_log (event, employee, invited) VALUES(?, ?, ?)");
            try {
                ps.setInt(1, event);
                ps.setInt(2, employee);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
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
        String eventName = request.getParameter("eventName");
        int eventID = Integer.parseInt(request.getParameter("event"));
        long employeeID = Long.parseLong(request.getParameter("employee"));
        Employee employee = sarariman.getDirectory().getByNumber().get(employeeID);
        String messageBody = "You are invited to the event '" + eventName + "'.\n\n" + "Go to " + request.getHeader("Referer") + " to RSVP.";
        String messageSubject = eventName;
        sarariman.getEmailDispatcher().send(employee.getEmail(), null, messageSubject, messageBody);
        try {
            logInvitation(eventID, (int)employeeID);
        } catch (SQLException se) {
            // FIXME: log this
            throw new IOException(se);
        }

        response.sendRedirect(request.getHeader("Referer"));
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "invites an employee to an event via email";
    }

}
