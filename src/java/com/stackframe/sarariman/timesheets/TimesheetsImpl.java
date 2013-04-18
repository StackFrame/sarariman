/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman.timesheets;

import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Sarariman;
import com.stackframe.sarariman.Week;
import java.net.URI;

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

}
