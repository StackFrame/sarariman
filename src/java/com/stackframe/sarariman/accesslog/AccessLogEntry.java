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
public interface AccessLogEntry {

    Timestamp getTimestamp();

    String getRemoteAddress();

    Employee getEmployee();

    int getStatus();

    String getPath();

    String getQuery();

    String getMethod();

    int getTime();

    String getUserAgent();

}
