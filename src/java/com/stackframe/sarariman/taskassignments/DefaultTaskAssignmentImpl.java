/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.taskassignments;

import com.stackframe.sarariman.tasks.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class DefaultTaskAssignmentImpl implements DefaultTaskAssignment {

    private final Task task;
    private final DataSource dataSource;

    DefaultTaskAssignmentImpl(Task task, DataSource dataSource) {
        this.task = task;
        this.dataSource = dataSource;
    }

    public Task getTask() {
        return task;
    }

    public boolean isFullTimeOnly() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT full_time_only FROM default_task_assignment WHERE task = ?");
                try {
                    s.setInt(1, task.getId());
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBoolean("full_time_only");
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

    public void setFullTimeOnly(boolean fullTimeOnly) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE default_task_assignment SET full_time_only = ? WHERE task = ?");
                try {
                    s.setBoolean(1, fullTimeOnly);
                    s.setInt(2, task.getId());
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.task.getId();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultTaskAssignmentImpl other = (DefaultTaskAssignmentImpl)obj;
        if (this.task.getId() != other.task.getId()) {
            return false;
        }
        return true;
    }

}
