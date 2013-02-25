/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.stackframe.collect.RangeUtilities;
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
class TimesheetEntriesImpl implements TimesheetEntries {

    private final DataSource dataSource;

    TimesheetEntriesImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Iterable<TimesheetEntry> getEntries(Range<Date> dateRange) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                String dateRangeExpression = RangeUtilities.toSQL("date", dateRange);
                PreparedStatement s = connection.prepareStatement(String.format("SELECT * FROM hours WHERE %s ORDER BY DATE DESC", dateRangeExpression));
                try {
                    ResultSet r = s.executeQuery();
                    try {
                        ImmutableList.Builder<TimesheetEntry> builder = ImmutableList.builder();
                        while (r.next()) {
                            int task = r.getInt("task");
                            int employee = r.getInt("employee");
                            Date date = r.getDate("date");
                            BigDecimal duration = r.getBigDecimal("duration");
                            int service_agreement = r.getInt("service_agreement");
                            String description = r.getString("description");
                            TimesheetEntry entry = new TimesheetEntryImpl(task, employee, date, duration, service_agreement, description);
                            builder.add(entry);
                        }

                        return builder.build();
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

}
