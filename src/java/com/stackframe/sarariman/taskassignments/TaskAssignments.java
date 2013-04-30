/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.tasks.Task;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface TaskAssignments {

    TaskAssignment get(Employee employee, Task task);

    TaskAssignment create(Employee employee, Task task);

    Map<Employee, Map<Task, TaskAssignment>> getMap();

}
