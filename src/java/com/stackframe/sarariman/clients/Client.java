/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.clients;

/**
 *
 * @author mcculley
 */
public interface Client {

    int getId();

    String getName();

    void setName(String name);

    void delete();

}
