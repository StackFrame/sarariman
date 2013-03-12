/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import static com.stackframe.sql.SQLUtilities.convert;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcculley
 */
public class TimesheetImpl implements Timesheet {

    // FIXME: These hard coded task numbers should come from a config file.
    private static final int holidayTask = 4;
    private static final int PTOTask = 5;
    private final Sarariman sarariman;
    private final int employeeNumber;
    private final Week week;
    private final TimesheetEntries entries;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public TimesheetImpl(Sarariman sarariman, int employeeNumber, Week week, TimesheetEntries entries) {
        this.sarariman = sarariman;
        this.employeeNumber = employeeNumber;
        this.week = week;
        this.entries = entries;
    }

    public static TimesheetImpl lookup(Sarariman sarariman, int employeeNumber, Week week) {
        return new TimesheetImpl(sarariman, employeeNumber, week, sarariman.getTimesheetEntries());
    }

    @Override
    public double getRegularHours() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT SUM(hours.duration) AS total "
                + "FROM hours "
                + "WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY) AND hours.task != ? AND hours.task != ?");
        try {
            ps.setInt(1, employeeNumber);
            ps.setDate(2, convert(week.getStart().getTime()));
            ps.setDate(3, convert(week.getStart().getTime()));
            ps.setInt(4, holidayTask);
            ps.setInt(5, PTOTask);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return 0;
                } else {
                    String total = resultSet.getString("total");
                    return total == null ? 0 : Double.parseDouble(total);
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public double getTotalHours() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT SUM(hours.duration) AS total "
                + "FROM hours "
                + "WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY)");
        try {
            ps.setInt(1, employeeNumber);
            ps.setDate(2, convert(week.getStart().getTime()));
            ps.setDate(3, convert(week.getStart().getTime()));
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return 0;
                } else {
                    String total = resultSet.getString("total");
                    return total == null ? 0 : Double.parseDouble(total);
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public Map<Calendar, BigDecimal> getHoursByDay() throws SQLException {
        Map<Calendar, BigDecimal> map = new LinkedHashMap<Calendar, BigDecimal>();
        for (int i = 0; i < 7; i++) {
            Calendar calendar = week.getStart();
            calendar.add(Calendar.DATE, i);
            map.put(calendar, new BigDecimal(0));
        }

        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT duration, date "
                + "FROM hours "
                + "WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY)");
        try {
            ps.setInt(1, employeeNumber);
            ps.setDate(2, convert(week.getStart().getTime()));
            ps.setDate(3, convert(week.getStart().getTime()));
            ResultSet resultSet = ps.executeQuery();
            try {
                while (resultSet.next()) {
                    Date date = resultSet.getDate("date");
                    Calendar calendar = (Calendar)week.getStart().clone();
                    calendar.setTime(date);
                    BigDecimal duration = resultSet.getBigDecimal("duration");
                    map.put(calendar, map.get(calendar).add(duration));
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
        return map;
    }

    public List<TimesheetEntry> getEntries() throws SQLException {
        Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT task, date "
                + "FROM hours "
                + "WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY) "
                + "ORDER BY date, task");
        try {
            ps.setInt(1, employeeNumber);
            ps.setDate(2, convert(week.getStart().getTime()));
            ps.setDate(3, convert(week.getStart().getTime()));
            ResultSet resultSet = ps.executeQuery();
            try {
                List<TimesheetEntry> list = new ArrayList<TimesheetEntry>();
                while (resultSet.next()) {
                    Date date = resultSet.getDate("date");
                    int task = resultSet.getInt("task");
                    Calendar calendar = (Calendar)week.getStart().clone();
                    calendar.setTime(date);
                    TimesheetEntry entry = entries.get(sarariman.getTasks().get(task), employee, date);
                    list.add(entry);
                }

                return list;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    private double getHours(int task) throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT SUM(hours.duration) AS total "
                + "FROM hours "
                + "WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY) AND hours.task = ?");
        try {
            ps.setInt(1, employeeNumber);
            ps.setDate(2, convert(week.getStart().getTime()));
            ps.setDate(3, convert(week.getStart().getTime()));
            ps.setInt(4, task);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return 0;
                } else {
                    String total = resultSet.getString("total");
                    return total == null ? 0 : Double.parseDouble(total);
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public double getHours(Date day) throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT SUM(duration) AS total FROM hours WHERE employee=? AND date=?");
        try {
            ps.setInt(1, employeeNumber);
            ps.setDate(2, day);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return 0;
                } else {
                    String total = resultSet.getString("total");
                    return total == null ? 0 : Double.parseDouble(total);
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public double getPTOHours() throws SQLException {
        return getHours(PTOTask);
    }

    @Override
    public double getHolidayHours() throws SQLException {
        return getHours(holidayTask);
    }

    @Override
    public boolean isSubmitted() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT submitted_timestamp FROM timecards WHERE date = ? AND employee = ?");
        try {
            ps.setDate(1, convert(week.getStart().getTime()));
            ps.setInt(2, employeeNumber);
            ResultSet resultSet = ps.executeQuery();
            try {
                return resultSet.first();
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public Employee getApprover() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT approver FROM timecards WHERE date = ? AND employee = ?");
        try {
            ps.setDate(1, convert(week.getStart().getTime()));
            ps.setInt(2, employeeNumber);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return null;
                } else {
                    int employee = resultSet.getInt("approver");
                    return sarariman.getDirectory().getByNumber().get(employee);
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public Timestamp getApprovedTimestamp() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT approved_timestamp FROM timecards WHERE date = ? AND employee = ?");
        try {
            ps.setDate(1, convert(week.getStart().getTime()));
            ps.setInt(2, employeeNumber);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return null;
                } else {
                    return resultSet.getTimestamp("approved_timestamp");
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public Timestamp getSubmittedTimestamp() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT submitted_timestamp FROM timecards WHERE date = ? AND employee = ?");
        try {
            ps.setDate(1, convert(week.getStart().getTime()));
            ps.setInt(2, employeeNumber);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return null;
                } else {
                    return resultSet.getTimestamp("submitted_timestamp");
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public boolean isApproved() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT approved FROM timecards WHERE date = ? AND employee = ?");
        try {
            ps.setDate(1, convert(week.getStart().getTime()));
            ps.setInt(2, employeeNumber);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return false;
                } else {
                    return resultSet.getBoolean("approved");
                }
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public boolean approve(Employee user) {
        try {
            Connection connection = sarariman.openConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE timecards SET approved=true, approver=?, approved_timestamp=? WHERE date=? AND employee=?");
            try {
                ps.setInt(1, user.getNumber());
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setDate(3, convert(week.getStart().getTime()));
                ps.setInt(4, employeeNumber);
                int rowCount = ps.executeUpdate();
                if (rowCount != 1) {
                    logger.severe("update for week=" + week + " and employee=" + employeeNumber + " did not modify a row");
                    return false;
                } else {
                    Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
                    sarariman.getEmailDispatcher().send(employee.getEmail(), null, "timesheet approved",
                            "Timesheet approved for " + employee.getFullName() + " for week of " + week + ".");
                    return true;
                }
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException se) {
            logger.log(Level.SEVERE, "caught exception approving timesheet", se);
            return false;
        }
    }

    public static boolean approve(TimesheetImpl timesheet, Employee user) {
        return timesheet.approve(user);
    }

    @Override
    public boolean reject() {
        try {
            Connection connection = sarariman.openConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM timecards WHERE date=? AND employee=?");
            try {
                ps.setDate(1, convert(week.getStart().getTime()));
                ps.setInt(2, employeeNumber);
                int rowCount = ps.executeUpdate();
                if (rowCount != 1) {
                    logger.severe("reject for week=" + week + " and employee=" + employeeNumber + " did not modify a row");
                    return false;
                } else {
                    Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
                    sarariman.getEmailDispatcher().send(employee.getEmail(), null, "timesheet rejected",
                            "Timesheet rejected for " + employee.getFullName() + " for week of " + week + ".");
                    return true;
                }
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException se) {
            logger.log(Level.SEVERE, "caught exception rejecting timesheet", se);
            return false;
        }
    }

    public static boolean reject(TimesheetImpl timesheet) {
        return timesheet.reject();
    }

    @Override
    public boolean submit() {
        // FIXME: Check that no day has more than 24 hours.
        try {
            Connection connection = sarariman.openConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO timecards (employee, date, approved) values(?, ?, false)");
            try {
                ps.setInt(1, employeeNumber);
                ps.setDate(2, convert(week.getStart().getTime()));
                int rowCount = ps.executeUpdate();
                if (rowCount != 1) {
                    logger.severe("submit for week=" + week + " and employee=" + employeeNumber + " did not modify a row");
                    return false;
                } else {
                    Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
                    // FIXME: Add URL to timesheet.
                    sarariman.getEmailDispatcher().send(EmailDispatcher.addresses(sarariman.getApprovers()), null,
                            "timesheet submitted",
                            "Timesheet submitted for " + employee.getFullName() + " for week of " + week + ".");
                    return true;
                }
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException se) {
            logger.log(Level.SEVERE, "caught exception submitting timesheet", se);
            return false;
        }
    }

    public static boolean submit(TimesheetImpl timesheet) {
        return timesheet.submit();
    }

    @Override
    public String toString() {
        return "{employee=" + employeeNumber + ",week=" + week + "}";
    }

}
