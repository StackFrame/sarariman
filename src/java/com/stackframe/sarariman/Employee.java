/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Range;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntry;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tickets.Ticket;
import com.stackframe.sarariman.vacation.VacationEntry;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import javax.mail.internet.InternetAddress;
import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
public interface Employee extends Linkable {

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

    Set<Project> getProjectsAdministrativelyAssisting();

    Set<Project> getCurrentlyAssignedProjects();

    Iterable<Project> getRelatedProjects();

    BigDecimal getDirectRate();

    BigDecimal getDirectRate(Date date);

    Iterable<Task> getTasks();

    BigDecimal getPaidTimeOff();

    // FIXME: This needs to be parameterized and/or moved elsewhere
    BigDecimal getRecentEntryLatency();

    Iterable<Employee> getAdministrativeAssistants();

    Map<Week, Timesheet> getTimesheets();

    URL getPhotoURL();

    Iterable<VacationEntry> getUpcomingVacation();

    Iterable<OutOfOfficeEntry> getUpcomingOutOfOffice();

    Iterable<Ticket> getUnclosedTickets();

    BigDecimal getMonthlyHealthInsurancePremium();

    boolean isPayrollAdministrator();

    void setPayrollAdministrator(boolean payrollAdministrator);

    boolean isBenefitsAdministrator();

    void setBenefitsAdministrator(boolean benefitsAdministrator);

    // FIXME: Add gender
    // FIXME: Add race
    // FIXME: Add periods of employment
    // FIXME: Add total hours of service
}
