/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.CostData;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.Workdays;
import com.stackframe.sarariman.tasks.Task;

/**
 *
 * @author mcculley
 */
public class ProjectedExpenseImpl implements ProjectedExpense {

    private final Employee employee;
    private final PeriodOfPerformance pop;
    private final Task task;
    private final CostData cost;
    private final Workdays workdays;

    public ProjectedExpenseImpl(Employee employee, PeriodOfPerformance pop, Task task, CostData cost, Workdays workdays) {
        this.employee = employee;
        this.pop = pop;
        this.task = task;
        this.cost = cost;
        this.workdays = workdays;
    }

    public Employee getEmployee() {
        return employee;
    }

    public PeriodOfPerformance getPeriodOfPerformance() {
        return pop;
    }

    public Task getTask() {
        return task;
    }

    public CostData getCost() {
        return cost;
    }

    public int getHours() {
        return workdays.getWorkdays(pop).size() * 8;
    }

}
