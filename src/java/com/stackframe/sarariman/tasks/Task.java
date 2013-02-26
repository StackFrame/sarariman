/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

/**
 *
 * @author mcculley
 */
public interface Task {

    int getId();

    String getName();
    void setName(String name);

    boolean isBillable();
    void setBillable(boolean billable);

    boolean isActive();
    void setActive(boolean active);

    int getProject();
    void setProject(int project);

    int getLineItem();
    void setLineItem(int lineItem);

    String getDescription();
    void setDescription(String description);

    String getClientDesignation();
    void setClientDesignation(String clientDesignation);

}
