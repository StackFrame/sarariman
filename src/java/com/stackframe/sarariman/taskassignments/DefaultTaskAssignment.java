/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.stackframe.sarariman.tasks.Task;

/**
 *
 * @author mcculley
 */
public interface DefaultTaskAssignment {

    Task getTask();

    boolean isFullTimeOnly();

    void setFullTimeOnly(boolean fullTimeOnly);

}
