/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.accesslog;

import com.stackframe.sarariman.Employee;
import java.sql.Timestamp;

/**
 *
 * @author mcculley
 */
public class AccessLogEntryImpl implements AccessLogEntry {

    private final Timestamp timestamp;
    private final String remoteAddress;
    private final Employee employee;
    private final int status;
    private final String path;
    private final String query;
    private final String method;
    private final int time;
    private final String userAgent;

    public AccessLogEntryImpl(Timestamp timestamp, String remoteAddress, Employee employee, int status, String path, String query, String method, int time, String userAgent) {
        this.timestamp = timestamp;
        this.remoteAddress = remoteAddress;
        this.employee = employee;
        this.status = status;
        this.path = path;
        this.query = query;
        this.method = method;
        this.time = time;
        this.userAgent = userAgent;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public Employee getEmployee() {
        return employee;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getMethod() {
        return method;
    }

    public int getTime() {
        return time;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
