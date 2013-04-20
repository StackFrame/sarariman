/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface Holidays {

    boolean isHoliday(Date date);

    Iterable<Holiday> getUpcoming();
    
    Holiday getNext();

}
