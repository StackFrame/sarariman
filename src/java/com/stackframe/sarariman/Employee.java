/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Range;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntry;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.taskassignments.TaskAssignment;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tickets.Ticket;
import com.stackframe.sarariman.timesheets.Timesheet;
import com.stackframe.sarariman.vacation.VacationEntry;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.util.Collection;
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

    String getGivenName();

    String getSurname();

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

    PhoneNumber getMobile();

    int getAge();

    String getDisplayName();

    SortedSet<Employee> getReports();

    Set<Project> getProjectsAdministrativelyAssisting();

    Set<Project> getCurrentlyAssignedProjects();

    Iterable<Project> getRelatedProjects();

    BigDecimal getDirectRate();

    BigDecimal getDirectRate(Date date);

    Iterable<Task> getTasks();

    Iterable<Task> getAssignedTasks();

    BigDecimal getPaidTimeOff();

    // FIXME: This needs to be parameterized and/or moved elsewhere
    BigDecimal getRecentEntryLatency();

    Iterable<Employee> getAdministrativeAssistants();

    Map<Week, Timesheet> getTimesheets();

    Map<Task, TaskAssignment> getTaskAssignments();

    URL getPhotoURL();

    Iterable<VacationEntry> getUpcomingVacation();

    Iterable<OutOfOfficeEntry> getUpcomingOutOfOffice();

    Collection<Ticket> getUnclosedTickets();

    BigDecimal getMonthlyHealthInsurancePremium();

    boolean isPayrollAdministrator();

    void setPayrollAdministrator(boolean payrollAdministrator);

    boolean isBenefitsAdministrator();

    void setBenefitsAdministrator(boolean benefitsAdministrator);

    byte[] getPhoto();

    Iterable<URL> getProfileLinks();

    Iterable<String> getTitles();

    // FIXME: Add gender
    // FIXME: Add race
    // FIXME: Add periods of employment
    // FIXME: Add total hours of service
    Object getPresence();

    String getVcard();

}
