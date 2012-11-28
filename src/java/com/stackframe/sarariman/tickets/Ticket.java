/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class Ticket {

    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Connection openConnection() throws SQLException {
        try {
            DataSource source = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
            return source.getConnection();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private Directory getDirectory() {
        try {
            Directory directory = (Directory)new InitialContext().lookup("sarariman.directory");
            return directory;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Employee> getWatchers() throws SQLException {
        Collection<Employee> result = new ArrayList<Employee>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT employee FROM ticket_watcher WHERE ticket = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        Employee watcher = getDirectory().getByNumber().get(resultSet.getInt("employee"));
                        result.add(watcher);
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

        return result;
    }

    public Employee getEmployeeCreator() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT employee_creator FROM ticket WHERE id = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    boolean hasEmployeeCreator = resultSet.first();
                    if (!hasEmployeeCreator) {
                        return null;
                    } else {
                        return getDirectory().getByNumber().get(resultSet.getInt("employee_creator"));
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
    }

    public String getName() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT name FROM ticket_name WHERE ticket = ? ORDER BY updated DESC LIMIT 1");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    resultSet.first();
                    return resultSet.getString("name");
                } finally {
                    resultSet.close();
                }
            } finally {
                query.close();
            }
        } finally {
            connection.close();
        }
    }

    public String getStatus() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT status FROM ticket_status WHERE ticket = ? ORDER BY updated DESC LIMIT 1");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    resultSet.first();
                    return resultSet.getString("status");
                } finally {
                    resultSet.close();
                }
            } finally {
                query.close();
            }
        } finally {
            connection.close();
        }
    }

    private List<Detail> getAssignmentDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_assignment WHERE ticket = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("updated");
                        Employee assignee = getDirectory().getByNumber().get(resultSet.getInt("assignee"));
                        Employee assignor = getDirectory().getByNumber().get(resultSet.getInt("assignor"));
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

    private List<Detail> getCommentDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_comment WHERE ticket = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("updated");
                        String comment = resultSet.getString("comment");
                        Employee employee = getDirectory().getByNumber().get(resultSet.getInt("employee"));
                        Detail detail = new Detail(timestamp, employee, employee.getDisplayName() + " commented: <div>" + comment + "</div>");
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

    private List<Detail> getDescriptionDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_description WHERE ticket = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("updated");
                        String description = resultSet.getString("description");
                        Employee employee = getDirectory().getByNumber().get(resultSet.getInt("employee"));
                        Detail detail = new Detail(timestamp, employee, employee.getDisplayName() + " set description to: <div>" + description + "</div>");
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

    private List<Detail> getStatusDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_status WHERE ticket = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("updated");
                        String status = resultSet.getString("status");
                        Employee employee = getDirectory().getByNumber().get(resultSet.getInt("employee"));
                        Detail detail = new Detail(timestamp, employee, employee.getDisplayName() + " set status to " + status);
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
        Connection connection = openConnection();
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
                        Employee employee = getDirectory().getByNumber().get(employeeID);
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
        details.addAll(getDescriptionDetails());
        details.addAll(getStatusDetails());
        details.addAll(getCommentDetails());
        Collections.sort(details, new Comparator<Detail>() {
            public int compare(Detail o1, Detail o2) {
                return o1.timestamp.compareTo(o2.timestamp);
            }

        });
        return details;
    }

}
