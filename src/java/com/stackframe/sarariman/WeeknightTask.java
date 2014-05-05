/*
 * Copyright (C) 2009-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stackframe.sarariman.timesheets.TimesheetImpl;
import static com.stackframe.sql.SQLUtilities.convert;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Logger;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author mcculley
 */
public class WeeknightTask implements Runnable {

    private final Sarariman sarariman;

    private final Directory directory;

    private final EmailDispatcher emailDispatcher;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public WeeknightTask(Sarariman sarariman, Directory directory, EmailDispatcher emailDispatcher) {
        this.sarariman = sarariman;
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;
    }

    @Override
    public void run() {
        Calendar today = Calendar.getInstance();
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return;
        }

        java.util.Date todayDate = today.getTime();
        boolean isHoliday = sarariman.getHolidays().isHoliday(todayDate);
        Week week = DateUtils.week(DateUtils.weekStart(todayDate));
        for (Employee employee : directory.getByUserName().values()) {
            if (!employee.isActive()) {
                continue;
            }

            TimesheetImpl timesheet = new TimesheetImpl(sarariman, employee.getNumber(), week, sarariman.getTimesheetEntries(), sarariman.getTasks(), sarariman.getDataSource(), sarariman.getDirectory());
            if (!timesheet.isSubmitted()) {
                Collection<Integer> chainOfCommand = sarariman.getOrganizationHierarchy().getChainsOfCommand(employee.getNumber());
                Iterable<InternetAddress> chainOfCommandAddresses = EmailDispatcher.addresses(sarariman.employees(chainOfCommand));
                if (dayOfWeek == Calendar.FRIDAY) {
                    if (employee.isFulltime() || timesheet.getTotalHours() > 0) {
                        String message = "Please submit your timesheet for the week of " + week + " at " + sarariman.getMountPoint() + ".";
                        emailDispatcher.send(employee.getEmail(), chainOfCommandAddresses, "timesheet", message);
                        PhoneNumber mobile = employee.getMobile();
                        if (mobile != null) {
                            try {
                                sarariman.getSMSGateway().send(mobile, "Please submit your timesheet.");
                            } catch (Exception e) {
                                // FIXME: log
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (!isHoliday) {
                    double hoursRecorded = timesheet.getHours(convert(todayDate));
                    if (hoursRecorded == 0.0 && employee.isFulltime()) {
                        String message = "Please record your time if you worked today at " + sarariman.getMountPoint() + ".";
                        emailDispatcher.send(employee.getEmail(), chainOfCommandAddresses, "timesheet", message);
                        PhoneNumber mobile = employee.getMobile();
                        if (mobile != null) {
                            try {
                                sarariman.getSMSGateway().send(mobile, "Please record your time if you worked today.");
                            } catch (Exception e) {
                                // FIXME: log
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

}
