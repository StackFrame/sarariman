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
public interface ProjectedExpense {

    Employee getEmployee();

    PeriodOfPerformance getPeriodOfPerformance();

    Task getTask();

    BigDecimal getCost();

    BigDecimal getHours();

}
