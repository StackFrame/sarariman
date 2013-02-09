/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class EmployeeController extends HttpServlet {

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
        Employee user = (Employee)request.getAttribute("user");
        String employeeID = request.getParameter("employee");
        Employee employee = sarariman.getDirectory().getByNumber().get(Integer.parseInt(employeeID));
        String action = request.getParameter("action");
        if (action.equals("setAdministrator")) {
            if (!user.isAdministrator()) {
                response.sendError(401);
            } else {
                String administrator = request.getParameter("administrator");
                employee.setAdministrator("on".equals(administrator));
            }
        } else {
            throw new AssertionError(String.format("unexpected action '%s'", action));
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
        return "performs updates on employee";
    }

}
