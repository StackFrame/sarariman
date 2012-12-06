/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Employee;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author mcculley
 */
public interface Ticket {

    Collection<Employee> getAssignees() throws SQLException;

    Employee getEmployeeCreator() throws SQLException;

    List<Detail> getHistory() throws SQLException;

    int getId();

    String getName() throws SQLException;

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
