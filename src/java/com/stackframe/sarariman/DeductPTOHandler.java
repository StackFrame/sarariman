/*
 * Copyright (C) 2012-2013 StackFrame, LLC
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
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class DeductPTOHandler extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        dataSource = ((Sarariman)getServletContext().getAttribute("sarariman")).getDataSource();
    }

    private void deduct(String week, String[] employeeIDs, String[] PTOValues) throws SQLException {
        int numRows = employeeIDs.length;
        Connection connection = openConnection();
        try {
            connection.setAutoCommit(false);
            for (int i = 0; i < numRows; i++) {
                PreparedStatement deductPTO = connection.prepareStatement("INSERT INTO paid_time_off(employee, amount, comment, effective, source) VALUES (?, ?, ?, ?, ?)");
                deductPTO.setInt(1, Integer.parseInt(employeeIDs[i]));
                deductPTO.setDouble(2, -Double.parseDouble(PTOValues[i]));
                deductPTO.setString(3, "used PTO");
                deductPTO.setString(4, week);
                deductPTO.setString(5, "weeklyPTODeduction");
                deductPTO.execute();
            }

            connection.commit();
            connection.setAutoCommit(true);
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
        // FIXME: Abort if not administrator.
        String week = request.getParameter("week");
        String[] employeeIDs = request.getParameterValues("employee");
        String[] PTOValues = request.getParameterValues("PTO");
        if (employeeIDs.length != PTOValues.length) {
            throw new ServletException("length of employees does not match length of PTO");
        }

        try {
            deduct(week, employeeIDs, PTOValues);
        } catch (SQLException se) {
            throw new ServletException(se);
        }

        response.sendRedirect(String.format("PTOUsage.jsp?week=%s", week));
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Do PTO deduction";
    }

}
