/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.outofoffice;

import com.stackframe.sarariman.Directory;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class OutOfOfficeEntriesImpl implements OutOfOfficeEntries {

    private final DataSource dataSource;
    private final Directory directory;

    public OutOfOfficeEntriesImpl(DataSource dataSource, Directory directory) {
        this.dataSource = dataSource;
        this.directory = directory;
    }

    public OutOfOfficeEntry get(int id) {
        return new OutOfOfficeEntryImpl(id, dataSource, directory);
    }

}
