/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.SortedSet;
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

    void setAdministrator(boolean administrator);

    boolean isApprover();

    boolean isInvoiceManager();

    boolean isActive();

    LocalDate getBirthdate();

    int getAge();

    String getDisplayName();

    SortedSet<Employee> getReports();

    Iterable<Long> getRelatedProjects(); // FIXME: Get rid of stupid map from Long and make this Integer (or Project).

}
