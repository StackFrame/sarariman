/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.accesslog;

import java.sql.Timestamp;

/**
 *
 * @author mcculley
 */
public interface AccessLogEntry {

    Timestamp getTimestamp();

}
