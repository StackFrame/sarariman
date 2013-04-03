/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

import com.stackframe.sarariman.Linkable;
import com.stackframe.sarariman.projects.Project;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface Task extends Linkable {

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

    BigDecimal getExpendedHours();

    BigDecimal getExpendedLabor();

    BigDecimal getExpendedOtherDirectCosts();

    BigDecimal getInvoiced();

    BigDecimal getInvoicedHours();

    BigDecimal getInvoicedLabor();

    BigDecimal getInvoicedOtherDirectCosts();

    Collection<Task> getChildren();

    Task getParent();

}
