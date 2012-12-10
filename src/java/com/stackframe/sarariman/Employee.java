/*
 * Copyright (C) 2009-2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import javax.mail.internet.InternetAddress;
import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
public interface Employee {

    String getUserName();

    String getFullName();

    int getNumber();

    boolean isFulltime();

    InternetAddress getEmail();

    boolean isAdministrator();

    boolean isApprover();

    boolean isInvoiceManager();

    boolean isActive();

    LocalDate getBirthdate();

    int getAge();

    String getDisplayName();

}
