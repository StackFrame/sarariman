/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.events;

import com.stackframe.sarariman.*;
import java.io.IOException;
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
        long employeeID = Long.parseLong(request.getParameter("employee"));
        Employee employee = sarariman.getDirectory().getByNumber().get(employeeID);
        String messageBody = "You are invited to the event '" + eventName + "'.\n\n" + "Go to " + request.getHeader("Referer") + " to RSVP.";
        String messageSubject = eventName;        
        sarariman.getEmailDispatcher().send(employee.getEmail(), null, messageSubject, messageBody);
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
