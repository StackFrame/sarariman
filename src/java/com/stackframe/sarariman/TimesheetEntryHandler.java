/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import static com.google.common.base.Preconditions.*;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.timesheets.Timesheet;
import static com.stackframe.sql.SQLUtilities.convert;
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

    private void recordEntry(Connection connection, Employee employee, Task task, Date date, String description,
                             BigDecimal duration, String location) throws SQLException {
        // FIXME: Check that the entry does not already exist.
        // FIXME: Check that the task is assigned to the employee.
        PreparedStatement s = connection.prepareStatement("INSERT INTO hours (employee, task, date, description, duration, location) VALUES(?, ?, ?, ?, ?, ?)");
        try {
            s.setInt(1, employee.getNumber());
            s.setInt(2, task.getId());
            s.setDate(3, convert(date));
            s.setString(4, description);
            s.setBigDecimal(5, duration);
            s.setString(6, location);
            int numRows = s.executeUpdate();
            if (numRows != 1) {
                throw new IllegalStateException("error inserting entry");
            }
        } finally {
            s.close();
        }
    }

    private void logEntry(Connection connection, Employee employee, Task task, Date date, String reason, String remoteAddress, Employee remoteUser, BigDecimal duration, String location) throws SQLException {
        PreparedStatement s = connection.prepareStatement("INSERT INTO hours_changelog (employee, task, date, reason, remote_address, remote_user, duration, location) values(?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            s.setInt(1, employee.getNumber());
            s.setInt(2, task.getId());
            s.setDate(3, convert(date));
            s.setString(4, reason);
            s.setString(5, remoteAddress);
            s.setInt(6, remoteUser.getNumber());
            s.setBigDecimal(7, duration);
            s.setString(8, location);
            int numRows = s.executeUpdate();
            if (numRows != 1) {
                throw new IllegalStateException("error logging entry");
            }
        } finally {
            s.close();
        }
    }

    private void recordAndLogEntry(Employee employee, Task task, Date date, String reason, String remoteHost, Employee user,
                                   BigDecimal duration, String description, String location) throws SQLException {
        if (employee.getNumber() != user.getNumber()) {
            // FIXME: Add support for administrators to modify entries on behalf of users.
            throw new IllegalArgumentException("submitter must be user");
        }

        Connection connection = sarariman.getDataSource().getConnection();
        try {
            connection.setAutoCommit(false);
            recordEntry(connection, employee, task, date, description, duration, location);
            logEntry(connection, employee, task, date, reason, remoteHost, user, duration, location);
            connection.commit();
            connection.setAutoCommit(true);
        } finally {
            connection.close();
        }
    }

    private void validate(BigDecimal duration, Date date, Task task, Employee employee) throws SQLException {
        // FIXME: Check that the duration has acceptable fractional part.
        if (duration.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("duration must be greater than 0");
        }

        if (duration.compareTo(BigDecimal.valueOf(24)) > 0) {
            throw new IllegalArgumentException("duration must be less than one day");
        }

        Date now = new Date();
        final int PTOtask = sarariman.getPaidTimeOff().getPaidTimeOffTask().getId();
        if (date.after(now) && task.getId() != PTOtask) {
            throw new IllegalArgumentException("Cannot record non-PTO time in the future.");
        }

        checkArgument(task.isActive(), "task must be active");

        // FIXME: Check that the task is on the list of tasks assigned to the employee, including default tasks.

        Date weekStart = DateUtils.weekStart(date);
        Week week = DateUtils.week(weekStart);
        Timesheet timesheet = sarariman.getTimesheets().get(employee, week);
        checkArgument(!timesheet.isSubmitted(), "timesheet must not already be submitted");
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
        checkArgument(checkNotNull(dateParam).length() > 0, "must have a date");
        String durationParam = request.getParameter("duration");
        checkArgument(checkNotNull(durationParam).length() > 0, "duration must be positive");
        String taskParam = request.getParameter("task");
        checkArgument(checkNotNull(taskParam).length() > 0, "must have a task");
        String descriptionParam = request.getParameter("description");
        checkArgument(checkNotNull(descriptionParam).length() > 0, "must have a description");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Employee user = (Employee)checkNotNull(request.getAttribute("user"), "must have a user");
        BigDecimal duration = new BigDecimal(durationParam); // FIXME: Check for valid fraction.
        Task task = sarariman.getTasks().getMap().get(Integer.parseInt(taskParam)); // FIXME: Check that task is valid.
        String geolocation = request.getParameter("geolocation");
        if (geolocation.isEmpty()) {
            geolocation = null;
        }

        try {
            Date date = dateFormat.parse(dateParam);
            TimesheetEntry entry = sarariman.getTimesheetEntries().get(task, user, date);
            if (entry.exists()) {
                request.getSession().setAttribute("attemptedOverwrite", true);
                request.getSession().setAttribute("attemptedDuration", durationParam);
                request.getSession().setAttribute("attemptedDescription", descriptionParam);
                response.sendRedirect(entry.getURL().toString());
                return;
            }

            validate(duration, date, task, user);
            recordAndLogEntry(user, task, date, "Entry created.", request.getRemoteHost().toString(), user, duration,
                              descriptionParam, geolocation);
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
