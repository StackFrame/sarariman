/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.stackframe.sarariman.tasks.Task;
import java.util.Set;

/**
 *
 * @author mcculley
 */
public interface DefaultTaskAssignments {

    Set<DefaultTaskAssignment> getAll();

    DefaultTaskAssignment get(Task task);

}
