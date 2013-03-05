/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

import com.stackframe.sarariman.projects.Project;
import java.math.BigDecimal;

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

    Project getProject();

    void setProject(Project project);

    int getLineItem();

    void setLineItem(int lineItem);

    String getDescription();

    void setDescription(String description);

    String getClientDesignation();

    void setClientDesignation(String clientDesignation);

    BigDecimal getExpended();

}
