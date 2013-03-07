/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.tasks.Task;
import static com.stackframe.sql.SQLUtilities.convert;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TimesheetEntryImpl implements TimesheetEntry {

    private final Task task;
    private final Employee employee;
    private final Date date;
    private final DataSource dataSource;

    public TimesheetEntryImpl(DataSource dataSource, Task task, Employee employee, Date date) {
        this.dataSource = dataSource;
        this.task = task;
        this.employee = employee;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public Task getTask() {
        return task;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BigDecimal getDuration() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT duration FROM hours WHERE employee=? AND task=? AND date=?");
                try {
                    s.setInt(1, employee.getNumber());
                    s.setInt(2, task.getId());
                    s.setDate(3, convert(date));
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        if (!hasRow) {
                            return BigDecimal.ZERO;
                        } else {
                            return r.getBigDecimal("duration");
                        }
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getServiceAgreement() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT service_agreement FROM hours WHERE employee=? AND task=? AND date=?");
                try {
                    s.setInt(1, employee.getNumber());
                    s.setInt(2, task.getId());
                    s.setDate(3, convert(date));
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        if (!hasRow) {
                            return 0;
                        } else {
                            return r.getInt("service_agreement");
                        }
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT description FROM hours WHERE employee=? AND task=? AND date=?");
                try {
                    s.setInt(1, employee.getNumber());
                    s.setInt(2, task.getId());
                    s.setDate(3, convert(date));
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        if (!hasRow) {
                            return null;
                        } else {
                            return r.getString("description");
                        }
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "TimesheetEntryImpl{" + "task=" + task + ", employee=" + employee + ", date=" + date + '}';
    }

}
