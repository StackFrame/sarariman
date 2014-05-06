/*
 * Copyright (C) 2012-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Range;
import com.stackframe.collect.RangeUtilities;
import static com.stackframe.sql.SQLUtilities.convert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
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

    @Override
    public Iterable<Holiday> getUpcoming() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT date, description " +
                                                       "FROM holidays WHERE date >= DATE(NOW()) ORDER BY date")) {
                ImmutableSortedSet.Builder<Holiday> b = ImmutableSortedSet.naturalOrder();
                while (rs.next()) {
                    Date date = rs.getDate("date");
                    String description = rs.getString("description");
                    Holiday holiday = new HolidayImpl(date, description);
                    b.add(holiday);
                }

                return b.build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Holiday getNext() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT date, description FROM holidays " +
                                                   "WHERE date >= DATE(NOW()) ORDER BY date LIMIT 1")) {
            boolean hasRow = rs.first();
            assert hasRow;
            Date date = rs.getDate("date");
            String description = rs.getString("description");
            Holiday holiday = new HolidayImpl(date, description);
            return holiday;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isHoliday(Date date) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT date FROM holidays WHERE date = ?")) {
            statement.setDate(1, convert(date));
            try (ResultSet rs = statement.executeQuery()) {
                return rs.first();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final Predicate<Date> isHoliday = this::isHoliday;

    @Override
    public Predicate<Date> isHoliday() {
        return isHoliday;
    }

    @Override
    public Collection<Date> get(Range<Date> range) {
        String dateRangeExpression = RangeUtilities.toSQL("date", range);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format("SELECT date FROM holidays WHERE %s",
                                                                                     dateRangeExpression));
             ResultSet rs = statement.executeQuery()) {
            ImmutableList.Builder<Date> listBuilder = ImmutableList.<Date>builder();
            while (rs.next()) {
                listBuilder.add(rs.getDate("date"));
            }

            return listBuilder.build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
