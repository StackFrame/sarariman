/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.clients.Client;

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

    Client getClient();

    void setClient(Client client);

}
