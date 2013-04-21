/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.timesheets;

import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.EmailDispatcher;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Sarariman;
import com.stackframe.sarariman.TimesheetEntries;
import com.stackframe.sarariman.TimesheetEntry;
import com.stackframe.sarariman.Week;
import com.stackframe.sarariman.tasks.Tasks;
import static com.stackframe.sql.SQLUtilities.convert;
import java.math.BigDecimal;
import java.net.URI;
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
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TimesheetImpl extends AbstractLinkable implements Timesheet {

    // FIXME: This hard coded task number should come from a config file or something.
    private static final int holidayTask = 4;
    private final int PTOTask;
    private final int employeeNumber;
    private final Week week;
    private final TimesheetEntries entries;
    private final Tasks tasks;
    private final DataSource dataSource;
    private final Directory directory;
    private final Sarariman sarariman;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public TimesheetImpl(Sarariman sarariman, int employeeNumber, Week week, TimesheetEntries entries, Tasks tasks, DataSource dataSource, Directory directory) {
        this.sarariman = sarariman;
        this.employeeNumber = employeeNumber;
        this.week = week;
        this.entries = entries;
        this.tasks = tasks;
        this.dataSource = dataSource;
        this.directory = directory;
        this.PTOTask = sarariman.getPaidTimeOff().getPaidTimeOffTask().getId();
    }

    @Override
    public double getRegularHours() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT SUM(hours.duration) AS total " +
                        "FROM hours " +
                        "WHERE employee=? AND " +
                        "hours.date >= ? AND " +
                        "hours.date < DATE_ADD(?, INTERVAL 7 DAY) AND " +
                        "hours.task != ? AND " +
                        "hours.task != ?");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getTotalHours() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT SUM(hours.duration) AS total " +
                        "FROM hours " +
                        "WHERE employee=? AND " +
                        "hours.date >= ? AND " +
                        "hours.date < DATE_ADD(?, INTERVAL 7 DAY)");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Calendar, BigDecimal> getHoursByDay() {
        Map<Calendar, BigDecimal> map = new LinkedHashMap<Calendar, BigDecimal>();
        for (int i = 0; i < 7; i++) {
            Calendar calendar = week.getStart();
            calendar.add(Calendar.DATE, i);
            map.put(calendar, new BigDecimal(0));
        }

        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT duration, date " +
                        "FROM hours " +
                        "WHERE employee=? AND " +
                        "hours.date >= ? AND " +
                        "hours.date < DATE_ADD(?, INTERVAL 7 DAY)");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public List<TimesheetEntry> getEntries() {
        Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT task, date " +
                        "FROM hours " +
                        "WHERE employee=? AND " +
                        "hours.date >= ? AND " +
                        "hours.date < DATE_ADD(?, INTERVAL 7 DAY) " +
                        "ORDER BY date, task");
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
                            TimesheetEntry entry = entries.get(tasks.get(task), employee, date);
                            list.add(entry);
                        }

                        return list;
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private double getHours(int task) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT SUM(hours.duration) AS total " +
                        "FROM hours " +
                        "WHERE employee=? AND " +
                        "hours.date >= ? AND " +
                        "hours.date < DATE_ADD(?, INTERVAL 7 DAY) AND " +
                        "hours.task = ?");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getHours(Date day) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT SUM(duration) AS total " +
                        "FROM hours WHERE employee=? " +
                        "AND date=?");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getPTOHours() {
        return getHours(PTOTask);
    }

    @Override
    public double getHolidayHours() {
        return getHours(holidayTask);
    }

    @Override
    public boolean isSubmitted() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT submitted_timestamp " +
                        "FROM timecards " +
                        "WHERE date = ? AND " +
                        "employee = ?");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Employee getApprover() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT approver FROM timecards " +
                        "WHERE date = ? AND " +
                        "employee = ?");
                try {
                    ps.setDate(1, convert(week.getStart().getTime()));
                    ps.setInt(2, employeeNumber);
                    ResultSet resultSet = ps.executeQuery();
                    try {
                        if (!resultSet.first()) {
                            return null;
                        } else {
                            int employee = resultSet.getInt("approver");
                            return directory.getByNumber().get(employee);
                        }
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Timestamp getApprovedTimestamp() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT approved_timestamp " +
                        "FROM timecards " +
                        "WHERE date = ? AND " +
                        "employee = ?");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Timestamp getSubmittedTimestamp() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT submitted_timestamp " +
                        "FROM timecards " +
                        "WHERE date = ? AND " +
                        "employee = ?");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isApproved() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT approved " +
                        "FROM timecards " +
                        "WHERE date = ? AND " +
                        "employee = ?");
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
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean approve(Employee user) {
        try {
            Connection connection = dataSource.getConnection();
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
                    Employee employee = directory.getByNumber().get(employeeNumber);
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
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM timecards WHERE date=? AND employee=?");
            try {
                ps.setDate(1, convert(week.getStart().getTime()));
                ps.setInt(2, employeeNumber);
                int rowCount = ps.executeUpdate();
                if (rowCount != 1) {
                    logger.severe("reject for week=" + week + " and employee=" + employeeNumber + " did not modify a row");
                    return false;
                } else {
                    Employee employee = directory.getByNumber().get(employeeNumber);
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
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO timecards (employee, date, approved) values(?, ?, false)");
            try {
                ps.setInt(1, employeeNumber);
                ps.setDate(2, convert(week.getStart().getTime()));
                int rowCount = ps.executeUpdate();
                if (rowCount != 1) {
                    logger.severe("submit for week=" + week + " and employee=" + employeeNumber + " did not modify a row");
                    return false;
                } else {
                    Employee employee = directory.getByNumber().get(employeeNumber);
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

    public URI getURI() {
        return URI.create(String.format("%stimesheet?week=%s&employee=%d", sarariman.getMountPoint(), week.getName(), employeeNumber));
    }

    public Employee getEmployee() {
        return directory.getByNumber().get(employeeNumber);
    }

    public Week getWeek() {
        return week;
    }

    @Override
    public String toString() {
        return "{employee=" + employeeNumber + ",week=" + week + "}";
    }

}
