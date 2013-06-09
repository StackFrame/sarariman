/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.vcard.vCardRenderers;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class StaffAddressBookEntryServlet extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String extension = ".vcf";
        String employeeName = request.getPathInfo().substring(1);
        employeeName = employeeName.substring(0, employeeName.length() - extension.length());
        System.err.println("in doGet for StaffAddressBookEntryServlet. employeeName='" + employeeName + "'");
        Employee employee = sarariman.getDirectory().getByUserName().get(employeeName);
        PrintWriter out = response.getWriter();
        try {
            out.print(vCardRenderers.getvCardRenderer("3.0").render(employee.vCardSource()));
        } finally {
            out.close();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "servlet for a vCard for an employee";
    }

}
