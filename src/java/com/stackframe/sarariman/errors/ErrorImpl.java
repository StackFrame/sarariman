/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.errors;

import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ErrorImpl extends AbstractLinkable implements Error {

    private final int id;
    private final DataSource dataSource;
    private final String mountPoint;
    private final Directory directory;

    public ErrorImpl(int id, DataSource dataSource, String mountPoint, Directory directory) {
        this.id = id;
        this.dataSource = dataSource;
        this.mountPoint = mountPoint;
        this.directory = directory;
    }

    public int getId() {
        return id;
    }

    public String getStackTrace() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT exception FROM error_log WHERE id=?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        r.first();
                        return r.getString("exception");
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

    public Timestamp getTimestamp() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT timestamp FROM error_log WHERE id=?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        r.first();
                        return r.getTimestamp("timestamp");
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

    public Employee getEmployee() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT employee FROM error_log WHERE id=?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        r.first();
                        int employeeNumber = r.getInt("employee");
                        if (r.wasNull()) {
                            return null;
                        } else {
                            return directory.getByNumber().get(employeeNumber);
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

    public URI getURI() {
        return URI.create(String.format("%serrors/view.jsp?id=%d", mountPoint, id));
    }

}
