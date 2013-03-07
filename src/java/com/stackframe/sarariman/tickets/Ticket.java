/*
 * Copyright (C) 2012-2103 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Linkable;
import java.net.InetAddress;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author mcculley
 */
public interface Ticket extends Linkable {

    Timestamp getCreated();

    Collection<Employee> getAssignees() throws SQLException;

    Location getCreatorLocation();

    String getCreatorUserAgent();

    InetAddress getCreatorIPAddress();

    Employee getEmployeeCreator();

    List<Detail> getHistory() throws SQLException;

    int getId();

    String getName() throws SQLException;

    String getDescription() throws SQLException;

    Collection<Employee> getStakeholders() throws SQLException;

    String getStatus() throws SQLException;

    Collection<Employee> getWatchers() throws SQLException;

    public static class Detail {

        private final Timestamp timestamp;
        private final Employee employee;
        private final String text;

        public Detail(Timestamp date, Employee employee, String text) {
            this.timestamp = date;
            this.employee = employee;
            this.text = text;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public Employee getEmployee() {
            return employee;
        }

        public String getText() {
            return text;
        }

    }
}
