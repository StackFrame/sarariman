/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.stackframe.sarariman.taskassignments.TaskAssignment;
import com.stackframe.sarariman.taskassignments.TaskAssignments;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TaskAssignmentsGlobalAudit implements Audit {

    private final DataSource dataSource;
    private final Directory directory;
    private final Tasks tasks;
    private final TaskAssignments taskAssignments;

    public TaskAssignmentsGlobalAudit(DataSource dataSource, Directory directory, Tasks tasks, TaskAssignments taskAssignments) {
        this.dataSource = dataSource;
        this.directory = directory;
        this.tasks = tasks;
        this.taskAssignments = taskAssignments;
    }

    public String getDisplayName() {
        return "Task Assignments";
    }

    private Collection<TaskAssignment> unusedTaskAssignments() throws SQLException {
        Collection<TaskAssignment> c = new ArrayList<TaskAssignment>();

        Connection connection = dataSource.getConnection();
        try {
            Statement s = connection.createStatement();
            try {
                ResultSet r = s.executeQuery(
                        "SELECT task_assignments.employee, task_assignments.task FROM task_assignments " +
                        "LEFT JOIN hours ON hours.task = task_assignments.task AND hours.employee = task_assignments.employee " +
                        "WHERE date IS NULL");
                try {
                    while (r.next()) {
                        int employeeNumber = r.getInt("employee");
                        int taskNumber = r.getInt("task");
                        Employee employee = directory.getByNumber().get(employeeNumber);
                        Task task = tasks.get(taskNumber);
                        c.add(taskAssignments.get(employee, task));
                    }
                } finally {
                    r.close();
                }
            } finally {
                s.close();
            }
        } finally {
            connection.close();
        }

        return c;
    }

    public Collection<AuditResult> getResults() {
        try {
            ImmutableList.Builder<AuditResult> listBuilder = ImmutableList.<AuditResult>builder();
            Collection<TaskAssignment> unused = unusedTaskAssignments();
            for (TaskAssignment a : unused) {
                listBuilder.add(new AuditResult(AuditResultType.warning,
                                                String.format("task assignment for task %d to %s has never been used",
                                                              a.getTask().getId(), a.getEmployee().getDisplayName()),
                                                a.getURL()));
            }

            return listBuilder.build();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
