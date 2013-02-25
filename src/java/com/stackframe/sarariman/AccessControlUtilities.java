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
