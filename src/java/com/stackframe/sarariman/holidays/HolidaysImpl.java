/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import static com.stackframe.sql.SQLUtilities.convert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.SortedSet;
import javax.sql.DataSource;
import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
class HolidaysImpl implements Holidays {

    private final DataSource dataSource;

    HolidaysImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SortedSet<Holiday> getAll() throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet rs = statement.executeQuery("SELECT date, description FROM holidays ORDER BY date");
                try {
                    ImmutableSortedSet.Builder<Holiday> b = ImmutableSortedSet.naturalOrder();
                    while (rs.next()) {
                        Date date = rs.getDate("date");
                        String description = rs.getString("description");
                        Holiday holiday = new HolidayImpl(new LocalDate(date), description);
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
    }

    public boolean isHoliday(Date date) throws SQLException {
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
    }

    public SortedSet<Integer> getYears() throws SQLException {
        ImmutableSortedSet.Builder<Integer> b = ImmutableSortedSet.naturalOrder();
        Function<Holiday, Integer> getYear = new Function<Holiday, Integer>() {
            public Integer apply(Holiday h) {
                return h.getDate().getYear();
            }

        };
        b.addAll(Collections2.transform(getAll(), getYear));
        return b.build();
    }

}
