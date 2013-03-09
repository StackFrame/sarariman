/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.events;

/**
 *
 * @author mcculley
 */
public interface Events {

    Event get(int id);

    Iterable<Event> getCurrent();

}
