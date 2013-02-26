/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Employee;

/**
 *
 * @author mcculley
 */
public interface Project {

    int getId();

    String getName();

    void setName(String name);

    boolean isManager(Employee employee);

    boolean isCostManager(Employee employee);

}
