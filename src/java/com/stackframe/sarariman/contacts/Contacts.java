/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.contacts;

import java.util.Set;

/**
 *
 * @author mcculley
 */
public interface Contacts {

    Contact get(int id);

    Set<Contact> getAll();

}
