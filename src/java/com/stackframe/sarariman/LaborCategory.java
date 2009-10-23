/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public class LaborCategory {

    private final long id;
    private final long project;
    private final BigDecimal rate;
    private final Date periodOfPerformanceStart;
    private final Date periodOfPerformanceEnd;
    private final String name;

    public LaborCategory(long id, long project, BigDecimal rate, Date periodOfPerformanceStart, Date periodOfPerformanceEnd, String name) {
        this.id = id;
        this.project = project;
        this.rate = rate;
        this.periodOfPerformanceStart = periodOfPerformanceStart;
        this.periodOfPerformanceEnd = periodOfPerformanceEnd;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public long getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public Date getPeriodOfPerformanceEnd() {
        return periodOfPerformanceEnd;
    }

    public Date getPeriodOfPerformanceStart() {
        return periodOfPerformanceStart;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "{id=" + id + ",project=" + project + ",rate=" + rate + ",name=" + name + ",popStart=" + periodOfPerformanceStart +
                ",popEnd=" + periodOfPerformanceEnd + "}";
    }

}
