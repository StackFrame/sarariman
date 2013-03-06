/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.DiscreteDomain;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

/**
 * Sarariman uses the week as a key for a lot of operations. Weeks begin on Saturday.
 *
 * @author mcculley
 */
public class Week implements Comparable<Week> {

    private final Calendar start;

    public Week(Calendar c) {
        checkArgument(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
        // Make a defensive copy to keep state immutable.
        this.start = (Calendar)c.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
    }

    private static DateFormat ISO8601DateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    private static Date parse(String name) {
        try {
            return ISO8601DateFormat().parse(name);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    public Week(String name) {
        this(parse(name));
    }

    private static Calendar calendar(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

    public Week(Date d) {
        this(calendar(d));
    }

    public Calendar getStart() {
        return (Calendar)start.clone();
    }

    public Calendar getEnd() {
        Calendar c = (Calendar)start.clone();
        c.add(Calendar.DATE, 6);
        return c;
    }

    public Week getNext() {
        Calendar c = (Calendar)start.clone();
        c.add(Calendar.DATE, 7);
        return new Week(c);
    }

    public Week getPrevious() {
        Calendar c = (Calendar)start.clone();
        c.add(Calendar.DATE, -7);
        return new Week(c);
    }

    public int compareTo(Week t) {
        return start.compareTo(t.start);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Get the name of the week. We consider the ISO 8601 form of the first day of the week (Saturday) to be the name.
     *
     * @return the name of the week
     */
    public String getName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(start.getTime());
    }

    public static final DiscreteDomain<Week> discreteDomain = new DiscreteDomain<Week>() {
        @Override
        public Week next(Week c) {
            return c.getNext();
        }

        @Override
        public Week previous(Week c) {
            return c.getPrevious();
        }

        @Override
        public long distance(Week start, Week end) {
            // FIXME: I'm not completely comforatble with this. Does it work with leap days between Saturdays? Need some unit tests.
            return Weeks.weeksBetween(new DateTime(start.getStart()), new DateTime(end.getStart())).getWeeks();
        }

        @Override
        public Week minValue() {
            return new Week("1970-01-03"); // FIXME: Come up with non-arbitrary bound.
        }

        @Override
        public Week maxValue() {
            return new Week("2030-01-05"); // FIXME: Come up with non-arbitrary bound.
        }

    };
}
