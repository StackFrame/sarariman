/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.TaskFinder;
import java.sql.SQLException;
import javax.sql.DataSource;

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
     * @param dataSource the DataSource to use to get data
     * @param entry the TimesheetEntry to test
     * @param user the Employee to test
     * @param organizationHierarchy the OrganizationHierarchy
     * @param directory the Directory
     * @return <code>true</code> if entry should be visible to user
     */
    public static boolean entryVisibleToUser(DataSource dataSource, TimesheetEntry entry, Employee user, OrganizationHierarchy organizationHierarchy, Directory directory) {
        try {
            Task task = new TaskFinder(dataSource, entry.getTask()).getTask();
            Project project = task.getProject();
            if (project == null) {
                return Sarariman.isBoss(organizationHierarchy, user);
            } else {
                return project.isManager(user) || project.isCostManager(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
