/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Linkable;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.tasks.Task;

/**
 *
 * @author mcculley
 */
public interface LaborProjections extends Linkable {

    LaborProjection get(int id);

    LaborProjection create(Employee employee, Task task, double utilization, PeriodOfPerformance pop);

}
