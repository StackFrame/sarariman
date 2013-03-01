/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import javax.sql.DataSource;

/**
 * Synchronizes a Directory with a database.
 *
 * @author mcculley
 */
interface DirectorySynchronizer {

    /**
     * Synchronize a Directory with the database.
     *
     * @throws Exception
     */
    void synchronize(Directory directory, DataSource dataSource) throws Exception;

}
