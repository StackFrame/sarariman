/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.clients;

import com.stackframe.sarariman.Linkable;

/**
 *
 * @author mcculley
 */
public interface Client extends Linkable {

    int getId();

    String getName();

    void setName(String name);

    void delete();

}
