/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.stackframe.base.Numbers;
import com.stackframe.sarariman.projects.Projects;
import com.stackframe.sarariman.tasks.Task;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TasksImpl implements Tasks {

    private final DataSource dataSource;
    private final String mountPoint;
    private final Projects projects;

    public TasksImpl(DataSource dataSource, String mountPoint, Projects projects) {
        this.dataSource = dataSource;
        this.mountPoint = mountPoint;
        this.projects = projects;
    }

    public Task get(int id) {
        return new TaskImpl(id, dataSource, mountPoint + "task", projects, this);
    }

    public Map<? extends Number, Task> getMap() {
        Function<Number, Task> f = new Function<Number, Task>() {
            public Task apply(Number f) {
                return get(f.intValue());
            }

        };
        return Maps.asMap(Numbers.positiveIntegers, f);
    }

    public Set<Task> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery("SELECT id FROM tasks");
                    try {
                        ImmutableSet.Builder<Task> builder = ImmutableSet.<Task>builder();
                        while (r.next()) {
                            builder.add(get(r.getInt("id")));
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
