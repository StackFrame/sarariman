/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

/**
 *
 * @author mcculley
 */
public class DateUtils {

    private DateUtils() {
    }

    public static Date now() {
        return new Date();
    }

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
        Collection<Date> result = new TreeSet<Date>();
        for (Date date : dates) {
            result.add(weekStart(date));
        }

        return result;
    }

    public static Date addDays(Date date, int days) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);
        startDate.add(Calendar.DATE, days);
        return startDate.getTime();
    }

    public static Date nextWeek(Date date) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);
        startDate.add(Calendar.DATE, 7);
        return startDate.getTime();
    }

    public static Date prevWeek(Date date) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);
        startDate.add(Calendar.DATE, -7);
        return startDate.getTime();
    }

}
