/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vacation;

import com.stackframe.sarariman.Directory;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class VacationsImpl implements Vacations {

    private final DataSource dataSource;
    private final Directory directory;

    public VacationsImpl(DataSource dataSource, Directory directory) {
        this.dataSource = dataSource;
        this.directory = directory;
    }

    public VacationEntry get(int id) {
        return new VacationEntryImpl(id, dataSource, directory);
    }

}
