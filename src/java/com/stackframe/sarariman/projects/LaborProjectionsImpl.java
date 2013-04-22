/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import static com.google.common.base.Preconditions.*;
import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import com.stackframe.sql.SQLUtilities;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LaborProjectionsImpl extends AbstractLinkable implements LaborProjections {

    private final DataSource dataSource;
    private final Directory directory;
    private final Tasks tasks;
    private final String mountPoint;

    public LaborProjectionsImpl(DataSource dataSource, Directory directory, Tasks tasks, String mountPoint) {
        this.dataSource = dataSource;
        this.directory = directory;
        this.tasks = tasks;
        this.mountPoint = mountPoint;
    }

    public LaborProjection get(int id) {
        return new LaborProjectionImpl(id, dataSource, directory, tasks, getURI().toString());
    }

    public LaborProjection create(Employee employee, Task task, double utilization, PeriodOfPerformance pop) {
        checkNotNull(employee);
        checkNotNull(task);
        checkArgument(utilization > 0 && utilization <= 1.0, "utilization must be in range (0,1]");
        checkNotNull(pop);
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement create = c.prepareStatement(
                        "INSERT INTO labor_projection (task, employee, pop_start, pop_end, utilization) VALUES(?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                try {
                    create.setInt(1, task.getId());
                    create.setInt(2, employee.getNumber());
                    create.setDate(3, SQLUtilities.convert(pop.getStart()));
                    create.setDate(4, SQLUtilities.convert(pop.getEnd()));
                    create.setDouble(5, utilization);
                    int createdRowCount = create.executeUpdate();
                    assert createdRowCount == 1;
                    ResultSet keys = create.getGeneratedKeys();
                    try {
                        keys.next();
                        int key = keys.getInt(1);
                        return get(key);
                    } finally {
                        keys.close();
                    }
                } finally {
                    create.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public URI getURI() {
        return URI.create(String.format("%slaborprojections/", mountPoint));
    }

}
