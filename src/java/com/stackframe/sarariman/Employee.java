/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Range;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.tasks.Task;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;
import java.util.SortedSet;
import javax.mail.internet.InternetAddress;
import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
public interface Employee extends Linkable, Identifiable {

    String getUserName();

    String getFullName();

    int getNumber();

    boolean isFulltime();

    Iterable<Range<Date>> getPeriodsOfService();

    boolean active(Date date);

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

    Iterable<Project> getRelatedProjects();

    BigDecimal getDirectRate();

    BigDecimal getDirectRate(Date date);

    Iterable<Task> getTasks();

    BigDecimal getPaidTimeOff();

    // FIXME: This needs to be parameterized and/or moved elsewhere
    BigDecimal getRecentEntryLatency();

    Iterable<Employee> getAdministrativeAssistants();

    Map<Week, Timesheet> getTimesheets();

}
