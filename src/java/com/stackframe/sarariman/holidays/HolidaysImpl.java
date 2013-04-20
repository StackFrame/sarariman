/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import com.google.common.collect.ImmutableSortedSet;
import static com.stackframe.sql.SQLUtilities.convert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class HolidaysImpl implements Holidays {

    private final DataSource dataSource;

    public HolidaysImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Iterable<Holiday> getUpcoming() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement statement = connection.createStatement();
                try {
                    ResultSet rs = statement.executeQuery("SELECT date, description FROM holidays WHERE date >= DATE(NOW()) ORDER BY date");
                    try {
                        ImmutableSortedSet.Builder<Holiday> b = ImmutableSortedSet.naturalOrder();
                        while (rs.next()) {
                            Date date = rs.getDate("date");
                            String description = rs.getString("description");
                            Holiday holiday = new HolidayImpl(date, description);
                            b.add(holiday);
                        }

                        return b.build();
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Holiday getNext() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement statement = connection.createStatement();
                try {
                    ResultSet rs = statement.executeQuery("SELECT date, description FROM holidays WHERE date >= DATE(NOW()) ORDER BY date LIMIT 1");
                    try {
                        boolean hasRow = rs.first();
                        assert hasRow;
                        Date date = rs.getDate("date");
                        String description = rs.getString("description");
                        Holiday holiday = new HolidayImpl(date, description);
                        return holiday;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isHoliday(Date date) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT date FROM holidays WHERE date = ?");
                try {
                    statement.setDate(1, convert(date));
                    ResultSet rs = statement.executeQuery();
                    try {
                        return rs.first();
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
