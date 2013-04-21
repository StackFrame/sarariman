/*
 * Copyright (C) 2010-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public PeriodOfPerformance(Range<Date> range) {
        this.start = range.lowerEndpoint();
        this.end = range.upperEndpoint();
    }

    public Date getEnd() {
        return end;
    }

    public Date getStart() {
        return start;
    }

    public Range<Date> asRange() {
        return Range.closed(start, end);
    }

    public PeriodOfPerformance intersection(PeriodOfPerformance pop) {
        return new PeriodOfPerformance(asRange().intersection(pop.asRange()));
    }

    public Collection<Date> getDays() {
        ImmutableList.Builder<Date> listBuilder = ImmutableList.<Date>builder();
        Calendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.set(Calendar.YEAR, start.getYear() + 1900);
        calendar.set(Calendar.MONTH, start.getMonth());
        calendar.set(Calendar.DATE, start.getDate());
        while (!calendar.getTime().after(end)) {
            Date date = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            listBuilder.add(date);
            calendar.add(Calendar.DATE, 1);
        }

        return listBuilder.build();
    }

    @Override
    public String toString() {
        return "{start=" + start + ", end=" + end + "}";
    }

}
