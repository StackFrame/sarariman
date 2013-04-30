/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.google.common.collect.ImmutableSet;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class DefaultTaskAssignmentsImpl implements DefaultTaskAssignments {

    private final DataSource dataSource;
    private final Tasks tasks;

    public DefaultTaskAssignmentsImpl(DataSource dataSource, Tasks tasks) {
        this.dataSource = dataSource;
        this.tasks = tasks;
    }

    public DefaultTaskAssignment get(Task task) {
        return new DefaultTaskAssignmentImpl(task, dataSource);
    }

    public Set<DefaultTaskAssignment> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery("SELECT task FROM default_task_assignment");
                    try {
                        ImmutableSet.Builder<DefaultTaskAssignment> builder = ImmutableSet.<DefaultTaskAssignment>builder();
                        while (r.next()) {
                            builder.add(get(tasks.get(r.getInt("task"))));
                        }

                        return builder.build();
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
