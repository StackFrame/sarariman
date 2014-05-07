/*
 * Copyright (C) 2009-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Years;

/**
 *
 * @author mcculley
 */
public class DateUtils {

    public static Date now() {
        return new Date();
    }

    public static Week week(Date date) {
        return new Week(weekStart(date));
    }

    // FIXME: Shouldn't this be in Week?
    public static Date weekStart(Date date) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);

        // Roll back to previous Saturday if not already on a Saturday.
        int day = startDate.get(Calendar.DAY_OF_WEEK);
        if (day != Calendar.SATURDAY) {
            startDate.add(Calendar.DATE, -day);
        }

        return startDate.getTime();
    }

    // FIXME: Shouldn't this be in Week?
    public static Date weekEnd(Date date) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);

        int day = startDate.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY) {
            startDate.add(Calendar.DATE, 6);
        } else {
            startDate.add(Calendar.DATE, Calendar.FRIDAY - day);
        }

        return startDate.getTime();
    }

    public static Collection<Date> weekStarts(Collection<Date> dates) {
        return dates.stream().map(DateUtils::weekStart).collect(Collectors.toSet());
    }

    public static Collection<Date> weekStarts(Date begin, Date end) {
        return weekStarts(new PeriodOfPerformance(begin, end).getDays());
    }

    public static Date addDays(Date date, int days) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);
        startDate.add(Calendar.DATE, days);
        return startDate.getTime();
    }

    public static int daysBetween(Date start, Date end) {
        DateTime x = new DateTime(start);
        DateTime y = new DateTime(end);
        return Days.daysBetween(x, y).getDays();
    }

    public static int yearsBetween(DateMidnight start, Date end) {
        DateTime y = new DateTime(end);
        return Years.yearsBetween(start, y).getYears();
    }

    private DateUtils() {
    }

}
