/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.tasks.Task;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TaskAssignmentImpl extends AbstractLinkable implements TaskAssignment {

    private final Employee employee;
    private final Task task;
    private final DataSource dataSource;
    private final String mountPoint;

    TaskAssignmentImpl(Employee employee, Task task, DataSource dataSource, String mountPoint) {
        this.employee = employee;
        this.task = task;
        this.dataSource = dataSource;
        this.mountPoint = mountPoint;
    }

    public URI getURI() {
        return URI.create(String.format("%staskassignment?employee=%d&task=%d", mountPoint, employee.getNumber(), task.getId()));
    }

    public Employee getEmployee() {
        return employee;
    }

    public Task getTask() {
        return task;
    }

    public void delete() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM task_assignments WHERE employee=? AND task=?");
                try {
                    ps.setInt(1, employee.getNumber());
                    ps.setInt(2, task.getId());
                    ps.executeUpdate();
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

}
