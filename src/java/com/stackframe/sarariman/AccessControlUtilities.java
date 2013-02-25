/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.SQLException;

/**
 *
 * @author mcculley
 */
public class AccessControlUtilities {

    private AccessControlUtilities() {
    }

    /**
     * Determines if a timesheet entry should be visible to a specified user.
     *
     * @param sarariman the instance of Sarariman to use to get data
     * @param entry the TimesheetEntry to test
     * @param user the Employee to test
     * @return 
     */
    public static boolean entryVisibleToUser(Sarariman sarariman, TimesheetEntry entry, Employee user) {
        try {
            Task task = Task.getTask(sarariman, entry.getTask());
            Project project = task.getProject();
            if (project == null) {
                return sarariman.isBoss(user);
            } else {
                return project.isManager(user) || project.isCostManager(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
