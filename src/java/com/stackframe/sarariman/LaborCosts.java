/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

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
public class LaborCosts extends HttpServlet {

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
            out.println("Employee,Task,Line Item,Labor Category,Date,Rate,Duration,Cost");
            PreparedStatement ps = connection.prepareStatement("SELECT i.employee, i.task, i.date, h.duration, t.project, t.line_item "
                    + "FROM invoices AS i "
                    + "JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date "
                    + "JOIN tasks AS t on i.task = t.id "
                    + "JOIN projects AS p ON t.project = p.id "
                    + "WHERE i.id = ? AND t.billable = TRUE "
                    + "ORDER BY h.date ASC, h.employee ASC, h.task ASC");
            ps.setString(1, invoice);
            try {
                ResultSet resultSet = ps.executeQuery();
                try {
                    while (resultSet.next()) {
                        int employeeNumber = resultSet.getInt("employee");
                        Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
                        int task = resultSet.getInt("task");
                        int lineItem = resultSet.getInt("line_item");
                        Date date = resultSet.getDate("date");
                        double duration = resultSet.getDouble("duration");
                        BigDecimal scaledDuration = new BigDecimal(duration).setScale(2);
                        int project = resultSet.getInt("project");
                        CostData costData = Invoice.cost(sarariman, project, employeeNumber, date, duration);
                        out.println("\"" + employee.getFullName() + "\"," + task + "," + lineItem + "," + costData.getLaborCategory().getName()
                                + "," + date + "," + costData.getRate() + "," + scaledDuration + "," + costData.getCost());
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
        return "Generates invoiced labor costs in CSV format";
    }

}
