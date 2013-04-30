/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Linkable;
import com.stackframe.sarariman.tasks.Task;

/**
 *
 * @author mcculley
 */
public interface TaskAssignment extends Linkable {

    Employee getEmployee();

    Task getTask();

    void delete();

}
