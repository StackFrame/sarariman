/*
 * Copyright (C) 2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Date;

/**
 *
 * @author mcculley
 */
public class PeriodOfPerformance {

    private final Date start;
    private final Date end;

    public PeriodOfPerformance(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getEnd() {
        return end;
    }

    public Date getStart() {
        return start;
    }

    @Override
    public String toString() {
        return "{start=" + start + ", end=" + end + "}";
    }

}
