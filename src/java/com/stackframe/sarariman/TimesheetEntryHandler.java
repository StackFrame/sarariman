/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.tasks.Task;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
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
public class TimesheetEntryHandler extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private void recordEntry(Connection connection, Employee employee, Task task, Date date, String description, BigDecimal duration) throws SQLException {
        // FIXME: Check that the entry does not already exist.
        // FIXME: Check that the employee is the user or is authorized to make changes on behalf of user.
        // FIXME: Check that the task is active.
        // FIXME: Check that the task is assigned to the employee.
        // FIXME: Check that the duration is greater than 0.
        // FIXME: Check that the duration is less than or equal to 24.0 hours.
        // FIXME: Check that the date is not in the future unless it is PTO.
        // FIXME: Check that the timesheet is not already submitted.
        // FIXME: Check that description is not empty.
        PreparedStatement s = connection.prepareStatement("INSERT INTO hours (employee, task, date, description, duration) VALUES(?, ?, ?, ?, ?)");
        try {
            s.setInt(1, employee.getNumber());
            s.setInt(2, task.getId());
            s.setDate(3, new java.sql.Date(date.getTime()));
            s.setString(4, description);
            s.setBigDecimal(5, duration);
            int numRows = s.executeUpdate();
            if (numRows != 1) {
                throw new IllegalStateException("error inserting entry");
            }
        } finally {
            s.close();
        }
    }

    private void logEntry(Connection connection, Employee employee, Task task, Date date, String reason, String remoteAddress, Employee remoteUser, BigDecimal duration) throws SQLException {
        PreparedStatement s = connection.prepareStatement("INSERT INTO hours_changelog (employee, task, date, reason, remote_address, remote_user, duration) values(?, ?, ?, ?, ?, ?, ?)");
        try {
            s.setInt(1, employee.getNumber());
            s.setInt(2, task.getId());
            s.setDate(3, new java.sql.Date(date.getTime()));
            s.setString(4, reason);
            s.setString(5, remoteAddress);
            s.setInt(6, remoteUser.getNumber());
            s.setBigDecimal(7, duration);
            int numRows = s.executeUpdate();
            if (numRows != 1) {
                throw new IllegalStateException("error logging entry");
            }
        } finally {
            s.close();
        }
    }

    private void recordAndLogEntry(Employee employee, Task task, Date date, String reason, String remoteHost, Employee user,
            BigDecimal duration, String description) throws SQLException {
        Connection connection = sarariman.getDataSource().getConnection();
        try {
            connection.setAutoCommit(false);
            recordEntry(connection, employee, task, date, description, duration);
            logEntry(connection, employee, task, date, reason, remoteHost, user, duration);
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
        String dateParam = request.getParameter("date");
        String durationParam = request.getParameter("duration");
        String taskParam = request.getParameter("task");
        String descriptionParam = request.getParameter("description");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Employee user = (Employee)request.getAttribute("user");
        BigDecimal duration = new BigDecimal(durationParam); // FIXME: Check for valid fraction.
        Task task = sarariman.getTasks().getMap().get(Integer.parseInt(taskParam)); // FIXME: Check that task is valid.
        try {
            Date date = dateFormat.parse(dateParam);
            recordAndLogEntry(user, task, date, "Entry created.", request.getRemoteHost().toString(), user, duration,
                    descriptionParam);
        } catch (ParseException pe) {
            throw new ServletException(pe);
        } catch (SQLException se) {
            throw new ServletException(se);
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
        return "handler for timesheet entries";
    }

}
