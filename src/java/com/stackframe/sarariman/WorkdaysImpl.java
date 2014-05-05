/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.holidays.Holidays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author mcculley
 */
public class WorkdaysImpl implements Workdays {

    private final Holidays holidays;

    public WorkdaysImpl(Holidays holidays) {
        this.holidays = holidays;
    }

    private static final Predicate<Date> isWeekDay = (Date t) -> {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(t);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day != Calendar.SATURDAY && day != Calendar.SUNDAY;
    };

    @Override
    public Collection<Date> getWorkdays(PeriodOfPerformance pop) {
        Collection<Date> holidaysToExclude = holidays.get(pop.asRange());
        Collection<Date> allDays = pop.getDays();
        Collection<Date> weekDays = allDays.stream().filter(isWeekDay).collect(Collectors.toList());
        Collection<Date> workingDays = new ArrayList<>(weekDays);
        workingDays.removeAll(holidaysToExclude);
        return workingDays;
    }

}
