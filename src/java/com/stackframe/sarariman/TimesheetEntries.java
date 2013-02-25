/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Range;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface TimesheetEntries {

    Iterable<TimesheetEntry> getEntries(Range<Date> dateRange);

}
