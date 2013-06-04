/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public interface Log {

    void log(long timestamp, String priority, String source, String message, String exception);

}
