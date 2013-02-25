/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface TimesheetEntry {

    int getTask();

    int getEmployee();

    Date getDate();

    BigDecimal getDuration();

    String getDescription();

    int getServiceAgreement();

}
