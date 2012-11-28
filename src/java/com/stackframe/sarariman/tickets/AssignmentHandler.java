/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Sarariman;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.mail.internet.InternetAddress;
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
public class AssignmentHandler extends HttpServlet {

    private Connection openConnection() throws SQLException {
        try {
            DataSource source = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
            return source.getConnection();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void assign(int ticket, int assignee, int assigner, int assignment) throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO ticket_assignment (ticket, assignee, assignor, assignment) VALUES(?, ?, ?, ?)");
            try {
                ps.setInt(1, ticket);
                ps.setInt(2, assignee);
                ps.setInt(3, assigner);
                ps.setInt(4, assignment);
                ps.execute();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    private void sendEmail(int assigneeID, Employee assignor, int ticket, String referer, int assignment) {
        String messageBody;
        String messageSubject;
        if (assignment == 1) {
            messageBody = String.format("%s assigned ticket %d to you.\n\nGo to %s to view.", assignor.getDisplayName(), ticket, referer);
            messageSubject = String.format("ticket %d: assigned", ticket);
        } else if (assignment == -1) {
            messageBody = String.format("%s unassigned ticket %d from you.\n\nGo to %s to view.", assignor.getDisplayName(), ticket, referer);
            messageSubject = String.format("ticket %d: unassigned", ticket);
        } else {
            throw new AssertionError("unexpected assignment value: " + assignment);
        }

        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        Employee assignee = sarariman.getDirectory().getByNumber().get(assigneeID);
        Collection<InternetAddress> cc = new ArrayList<InternetAddress>();
        cc.add(assignor.getEmail());
        sarariman.getEmailDispatcher().send(assignee.getEmail(), cc, messageSubject, messageBody);
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
        int assigneeID = Integer.parseInt(request.getParameter("assignee"));
        int assignment = Integer.parseInt(request.getParameter("assignment"));
        Employee assignor = (Employee)request.getAttribute("user");
        try {
            assign(ticket, assigneeID, assignor.getNumber(), assignment);
            if (assigneeID != assignor.getNumber()) {
                sendEmail(assigneeID, assignor, ticket, request.getHeader("Referer"), assignment);
            }

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
        return "handles changes in status of a ticket";
    }

}
