/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TaskFinder {

    private DataSource dataSource;
    private int id;

    public TaskFinder() {
    }

    public TaskFinder(DataSource dataSource, int id) {
        this.dataSource = dataSource;
        this.id = id;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask() {
        if (id == 0 || dataSource == null) {
            throw new IllegalStateException();
        }

        return new TaskImpl(id, dataSource);
    }

}
