/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import java.sql.SQLException;
import java.util.Date;
import java.util.SortedSet;

/**
 *
 * @author mcculley
 */
public interface Holidays {

    SortedSet<Holiday> getAll() throws SQLException;

    boolean isHoliday(Date date) throws SQLException;

    SortedSet<Integer> getYears() throws SQLException;
    
    Iterable<Holiday> getUpcoming();
    
    Holiday getNext();

}
