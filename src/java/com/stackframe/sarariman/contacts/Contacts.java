/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.contacts;

import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface Contacts {

    Collection<Contact> getAll() throws SQLException;

}
