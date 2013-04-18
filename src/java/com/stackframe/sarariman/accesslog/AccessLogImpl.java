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
                            AccessLogEntry e = new AccessLogEntryImpl(timestamp, remoteAddress, employee, status, path, query,
                                                                      method, time, userAgent);
                            c.add(e);
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
