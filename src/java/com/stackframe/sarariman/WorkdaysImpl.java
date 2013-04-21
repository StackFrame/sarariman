/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.stackframe.sarariman.holidays.Holidays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author mcculley
 */
public class WorkdaysImpl implements Workdays {

    private final Holidays holidays;

    public WorkdaysImpl(Holidays holidays) {
        this.holidays = holidays;
    }

    private static final Predicate<Date> isWeekDay = new Predicate<Date>() {
        public boolean apply(Date t) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(t);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            return day != Calendar.SATURDAY && day != Calendar.SUNDAY;
        }

    };
    private final Predicate<Date> isHoliday = new Predicate<Date>() {
        public boolean apply(Date t) {
            return holidays.isHoliday(t);
        }

    };

    public Collection<Date> getWorkdays(PeriodOfPerformance pop) {
        // FIXME: This could be faster if we pull holidays and remove them instead of the other way around.
        return Collections2.filter(Collections2.filter(pop.getDays(), isWeekDay), Predicates.not(isHoliday));
    }

}
