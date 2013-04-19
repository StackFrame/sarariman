/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.accesslog;

import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class AccessLogImpl implements AccessLog {

    private final DataSource dataSource;
    private final Directory directory;

    public AccessLogImpl(DataSource dataSource, Directory directory) {
        this.dataSource = dataSource;
        this.directory = directory;
    }

    public int getHitCount() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery(
                            "SELECT COUNT(timestamp) AS hits " +
                            "FROM access_log " +
                            "WHERE timestamp > DATE_SUB(NOW(), INTERVAL 1 DAY) AND remote_address NOT LIKE '0:0:0:0:0:0:0:1%0'");
                    try {
                        r.first();
                        return r.getInt("hits");
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public int getActiveUserCount() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery(
                            "SELECT COUNT(DISTINCT(employee)) AS active_users " +
                            "FROM access_log " +
                            "WHERE timestamp > DATE_SUB(NOW(), INTERVAL 5 MINUTE) AND " +
                            "remote_address NOT LIKE '0:0:0:0:0:0:0:1%0' AND " +
                            "path NOT LIKE '/statusboard/%' AND " +
                            "employee IS NOT NULL");
                    try {
                        r.first();
                        return r.getInt("active_users");
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public double getAverageTime() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery(
                            "SELECT AVG(time) AS average " +
                            "FROM access_log " +
                            "WHERE timestamp > DATE_SUB(NOW(), INTERVAL 1 DAY) AND remote_address NOT LIKE '0:0:0:0:0:0:0:1%0'");
                    try {
                        r.first();
                        return r.getDouble("average");
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    private AccessLogEntry read(ResultSet r) throws SQLException {
        Timestamp timestamp = r.getTimestamp("timestamp");
        String remoteAddress = r.getString("remote_address");
        int employeeNumber = r.getInt("employee");
        Employee employee;
        if (r.wasNull()) {
            employee = null;
        } else {
            employee = directory.getByNumber().get(employeeNumber);
        }

        int status = r.getInt("status");
        String path = r.getString("path");
        String query = r.getString("query");
        String method = r.getString("method");
        int time = r.getInt("time");
        String userAgent = r.getString("user_agent");
        return new AccessLogEntryImpl(timestamp, remoteAddress, employee, status, path, query, method, time, userAgent);
    }

    public Iterable<String> getUserAgents() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery(
                            "SELECT DISTINCT(user_agent) " +
                            "FROM access_log " +
                            "WHERE timestamp > DATE_SUB(NOW(), INTERVAL 1 DAY) " +
                            "ORDER BY user_agent");
                    try {
                        Collection<String> c = new ArrayList<String>();
                        while (r.next()) {
                            c.add(r.getString("user_agent"));
                        }

                        return c;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Iterable<AccessLogEntry> getLatest() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery(
                            "SELECT * " +
                            "FROM access_log " +
                            "WHERE timestamp > DATE_SUB(NOW(), INTERVAL 1 DAY) " +
                            "ORDER BY timestamp DESC");
                    try {
                        Collection<AccessLogEntry> c = new ArrayList<AccessLogEntry>();
                        while (r.next()) {
                            c.add(read(r));
                        }

                        return c;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Iterable<AccessLogEntry> getLongest() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery(
                            "SELECT * " +
                            "FROM access_log " +
                            "WHERE timestamp > DATE_SUB(NOW(), INTERVAL 1 DAY) AND remote_address NOT LIKE '0:0:0:0:0:0:0:1%0' " +
                            "GROUP BY path " +
                            "ORDER BY time DESC, timestamp DESC " +
                            "LIMIT 5");
                    try {
                        Collection<AccessLogEntry> c = new ArrayList<AccessLogEntry>();
                        while (r.next()) {
                            c.add(read(r));
                        }

                        return c;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
