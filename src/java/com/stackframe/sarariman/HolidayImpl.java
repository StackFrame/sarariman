/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Date;

/**
 *
 * @author mcculley
 */
public class HolidayImpl implements Holiday {

    private final Date date;
    private final String description;

    public HolidayImpl(Date date, String description) {
        this.date = date;
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

}
