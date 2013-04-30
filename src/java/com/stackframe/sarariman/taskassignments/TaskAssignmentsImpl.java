/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.tasks.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TaskAssignmentsImpl implements TaskAssignments {

    private final Directory directory;
    private final DataSource dataSource;
    private final String mountPoint;

    public TaskAssignmentsImpl(Directory directory, DataSource dataSource, String mountPoint) {
        this.directory = directory;
        this.dataSource = dataSource;
        this.mountPoint = mountPoint;
    }

    public TaskAssignment get(Employee employee, Task task) {
        return new TaskAssignmentImpl(employee, task, dataSource, mountPoint);
    }

    public TaskAssignment create(Employee employee, Task task) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO task_assignments (employee, task) VALUES(?, ?)");
                try {
                    ps.setInt(1, employee.getNumber());
                    ps.setInt(2, task.getId());
                    int numRows = ps.executeUpdate();
                    assert numRows == 1;
                    return get(employee, task);
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Map<Employee, Map<Task, TaskAssignment>> getMap() {
        return Maps.asMap(directory.getEmployees(), new Function<Employee, Map<Task, TaskAssignment>>() {
            public Map<Task, TaskAssignment> apply(final Employee e) {
                return e.getTaskAssignments();
            }

        });
    }

}
