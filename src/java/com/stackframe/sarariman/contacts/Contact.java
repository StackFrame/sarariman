/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.contacts;

import com.stackframe.sarariman.Linkable;
import java.sql.SQLException;

/**
 *
 * @author mcculley
 */
public interface Contact extends Linkable {

    int getId();

    String getName() throws SQLException;

}
