/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.timesheets;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Linkable;
import com.stackframe.sarariman.Week;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Timesheets extends Linkable {

    Timesheet get(Employee employee, Week week);

    Map<Employee, Map<Week, Timesheet>> getMap();

}
