/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Collection;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface Workdays {

    Collection<Date> getWorkdays(PeriodOfPerformance pop);

}
