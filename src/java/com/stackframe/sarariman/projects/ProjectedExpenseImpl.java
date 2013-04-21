/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.tasks.Task;
import java.math.BigDecimal;

/**
 *
 * @author mcculley
 */
public class ProjectedExpenseImpl implements ProjectedExpense {

    private final Employee employee;
    private final PeriodOfPerformance pop;
    private final Task task;
    private final BigDecimal cost;
    private final BigDecimal hours;

    public ProjectedExpenseImpl(Employee employee, PeriodOfPerformance pop, Task task, BigDecimal cost, BigDecimal hours) {
        this.employee = employee;
        this.pop = pop;
        this.task = task;
        this.cost = cost;
        this.hours = hours;
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

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getHours() {
        return hours;
    }

}