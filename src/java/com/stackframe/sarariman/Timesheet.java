/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Timesheet extends Linkable {

    boolean approve(Employee user);

    Timestamp getApprovedTimestamp();

    Employee getApprover();

    double getHolidayHours();

    double getHours(Date day);

    Map<Calendar, BigDecimal> getHoursByDay();

    List<TimesheetEntry> getEntries();

    double getPTOHours();

    double getRegularHours();

    Timestamp getSubmittedTimestamp();

    double getTotalHours();

    boolean isApproved();

    boolean isSubmitted();

    Employee getEmployee();

    Week getWeek();

    boolean reject();

    boolean submit();

}
