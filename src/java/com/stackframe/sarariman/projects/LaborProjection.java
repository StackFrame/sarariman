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
public interface LaborProjection extends Linkable {

    int getId();

    Employee getEmployee();

    PeriodOfPerformance getPeriodOfPerformance();

    void setPeriodOfPerformance(PeriodOfPerformance pop);

    Task getTask();
    
    void setTask(Task task);

    double getUtilization();

    void setUtilization(double utilization);

    void delete();

}
