/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public class TimesheetEntryImpl implements TimesheetEntry {

    private final int task;
    private final Employee employee;
    private final Date date;
    private final BigDecimal duration;
    private final int serviceAgreement;
    private final String description;

    public TimesheetEntryImpl(int task, Employee employee, Date date, BigDecimal duration, int serviceAgreement, String description) {
        this.task = task;
        this.employee = employee;
        this.date = date;
        this.duration = duration;
        this.serviceAgreement = serviceAgreement;
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public int getTask() {
        return task;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public int getServiceAgreement() {
        return serviceAgreement;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "TimesheetEntryImpl{" + "task=" + task + ", employee=" + employee + ", date=" + date + ", duration=" + duration + ", serviceAgreement=" + serviceAgreement + ", description=" + description + '}';
    }

}
