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
        java.util.Date todayDate = today.getTime();
        try {
            Date week = new Date(DateUtils.prevWeek(DateUtils.weekStart(todayDate)).getTime());
            for (Employee employee : directory.getByUserName().values()) {
                Timesheet timesheet = new Timesheet(sarariman, employee.getNumber(), week);
                if (!timesheet.isSubmitted()) {
                    emailDispatcher.send(employee.getEmail(), EmailDispatcher.addresses(sarariman.getApprovers()),
                            "late timesheet", "Please submit your timesheet for the week of " + week + ".");
                }
            }
        } catch (SQLException se) {
            logger.log(Level.SEVERE, "could not get hours for " + today, se);
        }
    }

}
