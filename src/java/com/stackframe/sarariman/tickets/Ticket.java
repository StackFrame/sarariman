/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Sarariman;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mcculley
 */
public class Ticket {

    private final int id;
    private final Sarariman sarariman;

    public static class Detail {

        public final Timestamp timestamp;
        public final Employee employee;
        public final String text;

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

    public Ticket(int id, Sarariman sarariman) {
        this.id = id;
        this.sarariman = sarariman;
    }

    private List<Detail> getAssignmentDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_assignment WHERE ticket = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("updated");
                        Employee assignee = sarariman.getDirectory().getByNumber().get(resultSet.getInt("assignee"));
                        Employee assignor = sarariman.getDirectory().getByNumber().get(resultSet.getInt("assignor"));
                        int assignment = resultSet.getInt("assignment");
                        Detail detail;
                        if (assignment == 1) {
                            detail = new Detail(timestamp, assignor, "assigned to " + assignee.getDisplayName() + " by " + assignor.getDisplayName());
                        } else if (assignment == -1) {
                            detail = new Detail(timestamp, assignor, "unassigned from " + assignee.getDisplayName() + " by " + assignor.getDisplayName());
                        } else {
                            throw new AssertionError("unexpected value for assignement: " + assignment);
                        }

                        details.add(detail);
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                query.close();
            }
        } finally {
            connection.close();
        }

        return details;
    }

    private List<Detail> getNameDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_name WHERE ticket = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("updated");
                        String name = resultSet.getString("name");
                        int employeeID = resultSet.getInt("employee");
                        Employee employee = sarariman.getDirectory().getByNumber().get(employeeID);
                        Detail detail = new Detail(timestamp, employee, "name was set to '" + name + "' by " + employee.getDisplayName());
                        details.add(detail);
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                query.close();
            }
        } finally {
            connection.close();
        }

        return details;
    }

    public List<Detail> getHistory() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        details.addAll(getNameDetails());
        details.addAll(getAssignmentDetails());
        Collections.sort(details, new Comparator<Detail>() {
            public int compare(Detail o1, Detail o2) {
                return o1.timestamp.compareTo(o2.timestamp);
            }

        });
        return details;
    }

}
