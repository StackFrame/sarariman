/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class TaskAssignmentController extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private enum Action {

        add, delete
    }

    private void createAssignment(int employee, int task) throws ServletException {
        try {
            Connection connection = sarariman.openConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO task_assignments (employee, task) VALUES(?, ?)");
                try {
                    ps.setInt(1, employee);
                    ps.setInt(2, task);
                    ps.executeUpdate();
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new ServletException(se);
        }
    }

    private void deleteAssignment(int employee, int task) throws ServletException {
        try {
            Connection connection = sarariman.openConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM task_assignments WHERE employee=? AND task=?");
                try {
                    ps.setInt(1, employee);
                    ps.setInt(2, task);
                    ps.executeUpdate();
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new ServletException(se);
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
        Employee user = (Employee)request.getAttribute("user");
        if (!user.isAdministrator()) {
            response.sendError(401);
            return;
        }

        Action action = Action.valueOf(request.getParameter("action"));
        try {
            int employee = Integer.parseInt(request.getParameter("employee"));
            int task = Integer.parseInt(request.getParameter("task"));
            switch (action) {
                case add:
                    createAssignment(employee, task);
                    break;
                case delete:
                    deleteAssignment(employee, task);
                    break;
                default:
                    response.sendError(500);
                    return;
            }

            response.sendRedirect(request.getHeader("Referer"));
            return;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "performs operations on the task assignment table";
    }

}
