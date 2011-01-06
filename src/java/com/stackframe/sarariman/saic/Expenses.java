/*
 * Copyright (C) 2011 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.saic;

import com.stackframe.sarariman.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class Expenses extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/csv;charset=UTF-8");
        String invoice = request.getParameter("id");
        Connection connection = sarariman.openConnection();
        PrintWriter out = response.getWriter();
        try {
            out.println("Employee,Task,Line Item,Charge Number,Date,Description,Cost");
            PreparedStatement ps = connection.prepareStatement("SELECT e.employee, e.task, e.date, e.cost, e.description, s.charge_number, s.po_line_item "
                    + "FROM expenses AS e "
                    + "JOIN tasks AS t on t.id = e.task "
                    + "LEFT JOIN saic_tasks AS s ON e.task = s.task "
                    + "WHERE e.invoice = ? "
                    + "ORDER BY s.po_line_item ASC, e.employee ASC, e.date ASC, e.task ASC, s.charge_number ASC");
            ps.setString(1, invoice);
            try {
                ResultSet resultSet = ps.executeQuery();
                try {
                    while (resultSet.next()) {
                        int employeeNumber = resultSet.getInt("employee");
                        Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
                        int task = resultSet.getInt("task");
                        int lineItem = resultSet.getInt("po_line_item");
                        String chargeNumber = resultSet.getString("charge_number");
                        Date date = resultSet.getDate("date");
                        double cost = resultSet.getDouble("cost");
                        String description = resultSet.getString("description");
                        out.println("\"" + employee.getFullName() + "\","
                                + task + "," + lineItem + "," + chargeNumber
                                + "," + date + "," + "\"" + description + "\"" +
                                "," + cost);
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                ps.close();
            }
        } catch (SQLException se) {
            throw new IOException(se);
        } finally {
            out.close();
            try {
                connection.close();
            } catch (SQLException se) {
                throw new ServletException(se);
            }
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Generates expenses in CSV format";
    }

}
