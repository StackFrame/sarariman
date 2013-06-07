/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executor;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LogImpl implements Log {

    private final DataSource dataSource;

    private final Executor databaseWriteExecutor;

    public LogImpl(DataSource dataSource, Executor databaseWriteExecutor) {
        this.dataSource = dataSource;
        this.databaseWriteExecutor = databaseWriteExecutor;
    }

    private void logSynchronous(long timestamp, String priority, String source, String message, String exception) {
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement(
                        "INSERT INTO log (priority, source, message, exception,`timestamp`) " +
                        "VALUES(?, ?, ?, ?, ?)");
                try {
                    s.setString(1, priority);
                    s.setString(2, source);
                    s.setString(3, message);
                    s.setString(4, exception);
                    s.setTimestamp(5, new Timestamp(timestamp));
                    int numRowsInserted = s.executeUpdate();
                    assert numRowsInserted == 1;
                } finally {
                    s.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            // This is the one place we can't log an exception.
            String formatted = String.format("%d %s %s %s %s", timestamp, priority, source, message, e.getMessage());
            System.err.println("Caught an exception trying to log: " + formatted);
            e.printStackTrace(System.err);
        }
    }

    public void log(final long timestamp, final String priority, final String source, final String message,
                    final String exception) {
        databaseWriteExecutor.execute(new Runnable() {
            public void run() {
                logSynchronous(timestamp, priority, source, message, exception);
            }

        });
    }

}
