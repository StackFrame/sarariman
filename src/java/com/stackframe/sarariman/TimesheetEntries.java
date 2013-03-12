/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Range;
import com.stackframe.sarariman.tasks.Task;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface TimesheetEntries {

    TimesheetEntry get(Task task, Employee employee, Date date);

    Iterable<TimesheetEntry> getEntries(Range<Date> dateRange);

}
