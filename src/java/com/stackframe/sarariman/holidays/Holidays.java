/*
 * Copyright (C) 2012-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.holidays;

import com.google.common.base.Predicate;
import com.google.common.collect.Range;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface Holidays {

    boolean isHoliday(Date date);

    Predicate<Date> isHoliday();

    Iterable<Holiday> getUpcoming();

    Holiday getNext();

    Collection<Date> get(Range<Date> range);

}
