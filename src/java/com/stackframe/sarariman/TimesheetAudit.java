/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author mcculley
 */
public class TimesheetAudit implements Audit {

    private final Sarariman sarariman;
    private final Directory directory;

    public TimesheetAudit(Sarariman sarariman, Directory directory) {
        this.sarariman = sarariman;
        this.directory = directory;
    }

    public String getDisplayName() {
        return "Timesheets";
    }

    private List<Timesheet> timesheets(Week week) {
        List<Timesheet> list = new ArrayList<Timesheet>();
        for (Employee employee : directory.getByUserName().values()) {
            Timesheet t = TimesheetImpl.lookup(sarariman, employee.getNumber(), week);
            list.add(t);
        }

        return list;
    }

    public Collection<AuditResult> getResults() {
        ImmutableList.Builder<AuditResult> listBuilder = ImmutableList.<AuditResult>builder();
        Week lastWeek = DateUtils.week(DateUtils.now()).getPrevious();
        List<Timesheet> timesheets = timesheets(lastWeek);
        // FIXME: Take throws SQLException out out timesheet.isSubmitted and isApproved
        try {
            for (Timesheet timesheet : timesheets) {
                if (timesheet.isSubmitted() && !timesheet.isApproved()) {
                    listBuilder.add(new AuditResult(AuditResultType.todo,
                                                    String.format("timesheet for %s needs review",
                                                                  timesheet.getEmployee().getDisplayName()),
                                                    timesheet.getURL()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return listBuilder.build();
    }

}
