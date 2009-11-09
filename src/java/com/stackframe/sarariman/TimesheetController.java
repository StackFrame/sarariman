/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class TimesheetController extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private enum Action {

        Approve, Reject
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Employee user = (Employee)request.getAttribute("user");
        if (!user.isApprover()) {
            response.sendError(401);
            return;
        }

        int employeeNumber = Integer.parseInt(request.getParameter("employee"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(request.getParameter("week"));
            Action action = Action.valueOf(request.getParameter("action"));
            System.err.println("action=" + action);
            Timesheet timesheet = Timesheet.lookup(sarariman, employeeNumber, date);
            switch (action) {
                case Approve:
                    timesheet.approve(user);
                    break;
                case Reject:
                    // FIXME: Only allow this if the time has not been invoiced.
                    timesheet.reject();
                    break;
                default:
                    response.sendError(500);
                    return;
            }

            response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("timesheet?employee={0}&week={1}", employeeNumber,
                    request.getParameter("week"))));
        } catch (Exception se) {
            IOException ioe = new IOException();
            ioe.initCause(se);
            throw ioe;
        }
    }

    /** 
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "performs updates on projects";
    }

}
