/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Tasks {

    Task get(int id);

    Iterable<Task> getAll();

    Map<? extends Number, Task> getMap();

}
