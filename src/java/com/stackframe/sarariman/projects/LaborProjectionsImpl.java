/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LaborProjectionsImpl implements LaborProjections {

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
        return new LaborProjectionImpl(id, dataSource, directory, tasks, mountPoint + "laborprojections/");

    }

    public LaborProjection create(Employee employee, Task task, double utilization, PeriodOfPerformance pop) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
