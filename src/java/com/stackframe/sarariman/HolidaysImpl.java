/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author mcculley
 */
class HolidaysImpl implements Holidays {

    private final ConnectionFactory connectionFactory;

    HolidaysImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public List<Holiday> getAll() throws SQLException {
        Connection connection = connectionFactory.openConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet rs = statement.executeQuery("SELECT date, description FROM holidays ORDER BY date");
                try {
                    List<Holiday> holidays = new ArrayList<Holiday>();
                    while (rs.next()) {
                        Date date = rs.getDate("date");
                        String description = rs.getString("description");
                        Holiday holiday = new HolidayImpl(date, description);
                        holidays.add(holiday);
                    }

                    return holidays;
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
        Connection connection = connectionFactory.openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT date FROM holidays WHERE date = ?");
            try {
                statement.setDate(1, new java.sql.Date(date.getTime()));
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
        ImmutableSortedSet.Builder b = ImmutableSortedSet.naturalOrder();
        Function<Holiday, Integer> getYear = new Function<Holiday, Integer>() {
            public Integer apply(Holiday h) {
                return h.getDate().getYear() + 1900;
            }

        };
        b.addAll(Collections2.transform(getAll(), getYear));
        return b.build();
    }

}
