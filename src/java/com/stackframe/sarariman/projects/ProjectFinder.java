/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectFinder {

    private DataSource dataSource;
    private int id;

    public ProjectFinder() {
    }

    public ProjectFinder(DataSource dataSource, int id) {
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

    public Project getProject() {
        if (id == 0 || dataSource == null) {
            throw new IllegalStateException();
        }

        return new ProjectImpl(id, dataSource);
    }

}
