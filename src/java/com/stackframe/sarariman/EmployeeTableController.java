/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class EmployeeTableController extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private enum Action {

        add, remove
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
        if (!user.isAdministrator()) {
            response.sendError(401);
            return;
        }

        Action action = Action.valueOf(request.getParameter("action"));
        long id = Long.parseLong(request.getParameter("employee"));
        Employee employee = sarariman.getDirectory().getByNumber().get(id);
        Collection<Employee> employeeCollection;
        String table = request.getParameter("table");
        if (table.equals("administrators")) {
            employeeCollection = sarariman.getAdministrators();
        } else if (table.equals("approvers")) {
            employeeCollection = sarariman.getApprovers();
        } else if (table.equals("invoiceManagers")) {
            employeeCollection = sarariman.getInvoiceManagers();
        } else {
            getServletContext().log("unknown table " + table);
            response.sendError(500);
            return;
        }

        switch (action) {
            case add:
                employeeCollection.add(employee);
                break;
            case remove:
                employeeCollection.remove(employee);
                break;
            default:
                response.sendError(500);
                return;
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
        return "performs updates on employee tables";
    }

}
