/*
 * Copyright (C) 2009-2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author mcculley
 */
public class WeeknightTask extends TimerTask {

    private final Sarariman sarariman;
    private final Directory directory;
    private final EmailDispatcher emailDispatcher;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public WeeknightTask(Sarariman sarariman, Directory directory, EmailDispatcher emailDispatcher) {
        this.sarariman = sarariman;
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;
    }

    public void run() {
        Calendar today = Calendar.getInstance();
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return;
        }

        java.util.Date todayDate = today.getTime();
        Date week = new Date(DateUtils.weekStart(todayDate).getTime());
        for (Employee employee : directory.getByUserName().values()) {
            if (!employee.isActive()) {
                continue;
            }

            Timesheet timesheet = new Timesheet(sarariman, employee.getNumber(), week);
            try {
                if (!timesheet.isSubmitted()) {
                    Collection<Integer> chainOfCommand = sarariman.getOrganizationHierarchy().getChainsOfCommand(employee.getNumber());
                    Collection<InternetAddress> chainOfCommandAddresses = EmailDispatcher.addresses(sarariman.employees(chainOfCommand));
                    if (dayOfWeek == Calendar.FRIDAY) {
                        String message = "Please submit your timesheet for the week of " + week + " at " + sarariman.getMountPoint() + ".";
                        emailDispatcher.send(employee.getEmail(), chainOfCommandAddresses, "timesheet", message);
                    } else {
                        double hoursRecorded = timesheet.getHours(new Date(todayDate.getTime()));
                        if (hoursRecorded == 0.0 && employee.isFulltime()) {
                            String message = "Please record your time if you worked today at " + sarariman.getMountPoint() + ".";
                            emailDispatcher.send(employee.getEmail(), chainOfCommandAddresses, "timesheet", message);
                        }
                    }
                }
            } catch (SQLException se) {
                logger.log(Level.SEVERE, "could not get hours for " + today, se);
            }
        }
    }

}
