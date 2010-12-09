/*
 * Copyright (C) 2009-2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcculley
 */
public class MorningTask extends TimerTask {

    private final Sarariman sarariman;
    private final Directory directory;
    private final EmailDispatcher emailDispatcher;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public MorningTask(Sarariman sarariman, Directory directory, EmailDispatcher emailDispatcher) {
        this.sarariman = sarariman;
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;
    }

    public void run() {
        Calendar today = Calendar.getInstance();
        Date prevWeek = new Date(DateUtils.prevWeek(DateUtils.weekStart(today.getTime())).getTime());
        // Check to see if last week's timesheet was submitted.
        for (Employee employee : directory.getByUserName().values()) {
            if (!employee.isActive()) {
                continue;
            }

            Timesheet timesheet = new Timesheet(sarariman, employee.getNumber(), prevWeek);
            try {
                if (!timesheet.isSubmitted() && (employee.isFulltime() || timesheet.getRegularHours() > 0)) {
                    emailDispatcher.send(employee.getEmail(), EmailDispatcher.addresses(sarariman.getApprovers()),
                            "late timesheet", "Please submit your timesheet for the week of " + prevWeek + ".");
                }
            } catch (SQLException se) {
                logger.log(Level.SEVERE, "could not get hours for " + today, se);
            }
        }
    }

}
