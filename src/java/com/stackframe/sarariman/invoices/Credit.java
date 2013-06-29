/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.invoices;

import com.stackframe.sarariman.projects.Project;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface Credit {

    int getId();

    Date getDate();

    BigDecimal getAmount();

    String getDescription();

    Project getProject();

    Invoice getInvoice();

}
