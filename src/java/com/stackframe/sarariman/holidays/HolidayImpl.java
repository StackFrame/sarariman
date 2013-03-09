/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import java.util.Date;

/**
 *
 * @author mcculley
 */
public class HolidayImpl implements Holiday {

    private final Date date;
    private final String description;

    HolidayImpl(Date date, String description) {
        this.date = date;
        this.description = description;
    }

    public Date getDate() {
        return (Date)date.clone();
    }

    public String getDescription() {
        return description;
    }

    public int compareTo(Holiday t) {
        return date.compareTo(t.getDate());
    }

}
