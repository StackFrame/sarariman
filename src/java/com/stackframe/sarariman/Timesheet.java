/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Timesheet {

    boolean approve(Employee user);

    Timestamp getApprovedTimestamp() throws SQLException;

    Employee getApprover() throws SQLException;

    double getHolidayHours() throws SQLException;

    double getHours(Date day) throws SQLException;

    Map<Calendar, BigDecimal> getHoursByDay() throws SQLException;

    List<TimesheetEntry> getEntries() throws SQLException;

    double getPTOHours() throws SQLException;

    double getRegularHours() throws SQLException;

    Timestamp getSubmittedTimestamp() throws SQLException;

    double getTotalHours() throws SQLException;

    boolean isApproved() throws SQLException;

    boolean isSubmitted() throws SQLException;

    boolean reject();

    boolean submit();

}
