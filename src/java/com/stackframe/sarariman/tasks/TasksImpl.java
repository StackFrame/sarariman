/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

import com.google.common.base.Function;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.OrganizationHierarchy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TasksImpl implements Tasks {

    private final DataSource dataSource;
    private final OrganizationHierarchy organizationHierarchy;
    private final Directory directory;

    public TasksImpl(DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory) {
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
    }

    public Map<? extends Number, Task> getMap() {
        // FIXME: Need to handle both Long and Integer
        Set<? extends Number> keys = ContiguousSet.create(Range.closed(1, Integer.MAX_VALUE), DiscreteDomain.integers());
        Function<Number, Task> f = new Function<Number, Task>() {
            public Task apply(Number f) {
                return new TaskImpl(f.intValue(), dataSource, organizationHierarchy, directory);
            }

        };
        return Maps.asMap(keys, f);
    }

    public Iterable<Task> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery("SELECT id FROM tasks");
                    try {
                        Collection<Task> c = new ArrayList<Task>();
                        while (r.next()) {
                            c.add(new TaskImpl(r.getInt("id"), dataSource, organizationHierarchy, directory));
                        }

                        return c;
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
