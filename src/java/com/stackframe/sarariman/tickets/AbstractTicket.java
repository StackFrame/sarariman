/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.HashSet;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public abstract class AbstractTicket implements Ticket {

    @Override
    public Collection<Employee> getAssignees() throws SQLException {
        Collection<Employee> result = new ArrayList<Employee>();
        Connection connection = openConnection();
        try {
            // FIXME: There must be a smarter way to do this in SQL instead of doing two queries. Self-join?
            PreparedStatement assigneeQuery = connection.prepareStatement("SELECT DISTINCT assignee FROM ticket_assignment WHERE ticket = ?");
            try {
                assigneeQuery.setInt(1, getId());
                ResultSet assigneeResultSet = assigneeQuery.executeQuery();
                try {
                    while (assigneeResultSet.next()) {
                        int assignee = assigneeResultSet.getInt("assignee");
                        PreparedStatement sumQuery = connection.prepareStatement("SELECT SUM(assignment) AS sum FROM ticket_assignment WHERE ticket = ? AND assignee = ?");
                        try {
                            sumQuery.setInt(1, getId());
                            sumQuery.setInt(2, assignee);
                            ResultSet sumResultSet = sumQuery.executeQuery();
                            sumResultSet.first();
                            try {
                                int sum = sumResultSet.getInt("sum");
                                if (sum > 0) {
                                    Employee watcher = getDirectory().getByNumber().get(assignee);
                                    result.add(watcher);
                                }
                            } finally {
                                sumResultSet.close();
                            }
                        } finally {
                            sumQuery.close();
                        }
                    }
                } finally {
                    assigneeResultSet.close();
                }
            } finally {
                assigneeQuery.close();
            }
        } finally {
            connection.close();
        }
        return result;
    }

    protected List<Detail> getAssignmentDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_assignment WHERE ticket = ?");
            try {
                query.setInt(1, getId());
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

    protected List<Detail> getCommentDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_comment WHERE ticket = ?");
            try {
                query.setInt(1, getId());
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

    protected Detail getCreationDetail() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT created, employee_creator FROM ticket WHERE id = ?");
            try {
                query.setInt(1, getId());
                ResultSet resultSet = query.executeQuery();
                try {
                    resultSet.next();
                    Timestamp created = resultSet.getTimestamp("created");
                    // FIXME: Handle case of no employee_creator when we support external creators.
                    int employeeID = resultSet.getInt("employee_creator");
                    Employee employee = getDirectory().getByNumber().get(employeeID);
                    return new Detail(created, employee, "created by " + employee.getDisplayName());
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

    protected List<Detail> getDescriptionDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_description WHERE ticket = ?");
            try {
                query.setInt(1, getId());
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

    protected Directory getDirectory() {
        try {
            Directory directory = (Directory)new InitialContext().lookup("sarariman.directory");
            return directory;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Detail> getHistory() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        details.add(getCreationDetail());
        details.addAll(getNameDetails());
        details.addAll(getAssignmentDetails());
        details.addAll(getDescriptionDetails());
        details.addAll(getStatusDetails());
        details.addAll(getCommentDetails());
        Collections.sort(details, new Comparator<Detail>() {
            public int compare(Detail o1, Detail o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }

        });
        return details;
    }

    @Override
    public String getName() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT name FROM ticket_name WHERE ticket = ? ORDER BY updated DESC LIMIT 1");
            try {
                query.setInt(1, getId());
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

    protected List<Detail> getNameDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_name WHERE ticket = ?");
            try {
                query.setInt(1, getId());
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

    @Override
    public Collection<Employee> getStakeholders() throws SQLException {
        Collection<Employee> result = new HashSet<Employee>();
        Employee creator = getEmployeeCreator();
        if (creator != null) {
            result.add(creator);
        }
        result.addAll(getWatchers());
        result.addAll(getAssignees());
        return result;
    }

    @Override
    public String getStatus() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT status FROM ticket_status WHERE ticket = ? ORDER BY updated DESC LIMIT 1");
            try {
                query.setInt(1, getId());
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

    protected List<Detail> getStatusDetails() throws SQLException {
        List<Detail> details = new ArrayList<Detail>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT * FROM ticket_status WHERE ticket = ?");
            try {
                query.setInt(1, getId());
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

    @Override
    public Collection<Employee> getWatchers() throws SQLException {
        Collection<Employee> result = new ArrayList<Employee>();
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT employee FROM ticket_watcher WHERE ticket = ?");
            try {
                query.setInt(1, getId());
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

    protected Connection openConnection() throws SQLException {
        try {
            DataSource source = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
            return source.getConnection();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
