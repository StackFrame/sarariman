/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.timesheets;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Sarariman;
import com.stackframe.sarariman.Week;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class TimesheetsImpl extends AbstractLinkable implements Timesheets {

    private final Sarariman sarariman;
    private final String mountPoint;

    public TimesheetsImpl(Sarariman sarariman, String mountPoint) {
        this.sarariman = sarariman;
        this.mountPoint = mountPoint;
    }

    public Timesheet get(Employee employee, Week week) {
        return new TimesheetImpl(sarariman, employee.getNumber(), week, sarariman.getTimesheetEntries(), sarariman.getTasks(), sarariman.getDataSource(), sarariman.getDirectory());
    }

    public URI getURI() {
        return URI.create(String.format("%stimesheets", mountPoint));
    }

    public Map<Employee, Map<Week, Timesheet>> getMap() {
        return Maps.asMap(sarariman.getDirectory().getEmployees(), new Function<Employee, Map<Week, Timesheet>>() {
            public Map<Week, Timesheet> apply(final Employee e) {
                return e.getTimesheets();
            }

        });
    }

}
