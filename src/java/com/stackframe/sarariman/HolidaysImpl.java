/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public boolean isHoliday(Date date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
