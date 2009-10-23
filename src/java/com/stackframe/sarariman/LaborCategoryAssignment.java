/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Date;

/**
 *
 * @author mcculley
 */
public class LaborCategoryAssignment {

    private final long laborCategory;
    private final Employee employee;
    private final Date periodOfPerformanceStart;
    private final Date periodOfPerformanceEnd;

    public LaborCategoryAssignment(long laborCategory, Employee employee, Date periodOfPerformanceStart, Date periodOfPerformanceEnd) {
        this.laborCategory = laborCategory;
        this.employee = employee;
        this.periodOfPerformanceStart = periodOfPerformanceStart;
        this.periodOfPerformanceEnd = periodOfPerformanceEnd;
    }

    public Employee getEmployee() {
        return employee;
    }

    public long getLaborCategory() {
        return laborCategory;
    }

    public Date getPeriodOfPerformanceEnd() {
        return periodOfPerformanceEnd;
    }

    public Date getPeriodOfPerformanceStart() {
        return periodOfPerformanceStart;
    }

    @Override
    public String toString() {
        return "{employee=" + employee + ",laborCategory=" + laborCategory + ",popStart=" + periodOfPerformanceStart + ",popEnd=" +
                periodOfPerformanceEnd + "}";
    }

}
