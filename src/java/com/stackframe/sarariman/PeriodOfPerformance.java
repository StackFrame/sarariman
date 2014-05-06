/*
 * Copyright (C) 2010-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 *
 * @author mcculley
 */
public class PeriodOfPerformance {

    private final Date start, end;

    public PeriodOfPerformance(String start, String end) throws ParseException {
        this(Week.ISO8601DateFormat().parse(start), Week.ISO8601DateFormat().parse(end));
    }

    public PeriodOfPerformance(Date start, Date end) {
        this.start = (Date)start.clone();
        this.end = (Date)end.clone();
        if (start.after(end)) {
            throw new IllegalArgumentException("end must be after start");
        }
    }

    public PeriodOfPerformance(Range<Date> range) {
        this.start = range.lowerEndpoint();
        this.end = range.upperEndpoint();
    }

    public Date getEnd() {
        return (Date)end.clone();
    }

    public Date getStart() {
        return (Date)start.clone();
    }

    public Range<Date> asRange() {
        return Range.closed(getStart(), getEnd());
    }

    public PeriodOfPerformance intersection(PeriodOfPerformance pop) {
        return new PeriodOfPerformance(asRange().intersection(pop.asRange()));
    }

    private static Calendar convert(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public Collection<Date> getDays() {
        ImmutableList.Builder<Date> listBuilder = ImmutableList.<Date>builder();
        Calendar calendar = new GregorianCalendar();
        calendar.clear();
        Calendar cStart = convert(start);
        calendar.set(Calendar.YEAR, cStart.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, cStart.get(Calendar.MONTH));
        calendar.set(Calendar.DATE, cStart.get(Calendar.DATE));
        while (!calendar.getTime().after(end)) {
            listBuilder.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }

        return listBuilder.build();
    }

    public static PeriodOfPerformance make(Date start, Date end) {
        return new PeriodOfPerformance(start, end);
    }

    @Override
    public String toString() {
        return "{start=" + start + ", end=" + end + "}";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.start);
        hash = 37 * hash + Objects.hashCode(this.end);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final PeriodOfPerformance other = (PeriodOfPerformance)obj;
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }

        return Objects.equals(this.end, other.end);
    }

}
