/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.contacts;

import java.sql.SQLException;

/**
 *
 * @author mcculley
 */
public interface Contact {

    int getId();

    String getName() throws SQLException;

}
