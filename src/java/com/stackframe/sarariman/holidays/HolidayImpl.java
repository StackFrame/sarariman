/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
public class HolidayImpl implements Holiday {

    private final LocalDate date;
    private final String description;

    HolidayImpl(LocalDate date, String description) {
        this.date = date;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int compareTo(Holiday t) {
        return date.compareTo(t.getDate());
    }

}
