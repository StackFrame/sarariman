/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.accesslog;

/**
 *
 * @author mcculley
 */
public interface AccessLog {

    int getHitCount();

    double getAverageTime();

    int getActiveUserCount();

    Iterable<String> getUserAgents();

    Iterable<AccessLogEntry> getLatest();

    Iterable<AccessLogEntry> getLongest();

}
