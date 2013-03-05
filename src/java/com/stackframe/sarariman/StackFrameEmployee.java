/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Range;
import com.stackframe.collect.RangeUtilities;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.tasks.Task;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.sql.DataSource;
import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
class StackFrameEmployee extends AbstractLinkable implements Employee {

    private final String fullName;
    private final String userName;
    private final int number;
    private final boolean fulltime;
    private final boolean active;
    private final String email;
    private final LocalDate birthdate;
    private final String displayName;
    private final Range<java.sql.Date> periodOfService;
    private final LDAPDirectory directory;
    private final DataSource dataSource;
    private final Sarariman sarariman;

    StackFrameEmployee(String fullName, String userName, int number, boolean fulltime, boolean active, String email, LocalDate birthdate, String displayName, Range<java.sql.Date> periodOfService, LDAPDirectory directory, DataSource dataSource, Sarariman sarariman) {
        this.directory = directory;
        this.fullName = fullName;
        this.userName = userName;
        this.number = number;
        this.fulltime = fulltime;
        this.active = active;
        this.email = email;
        this.birthdate = birthdate;
        this.displayName = displayName;
        this.periodOfService = periodOfService;
        this.dataSource = dataSource;
        this.sarariman = sarariman;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getNumber() {
        return number;
    }

    public boolean isFulltime() {
        return fulltime;
    }

    public boolean isActive() {
        return active;
    }

    public InternetAddress getEmail() {
        try {
            return new InternetAddress(email, true);
        } catch (AddressException ae) {
            throw new RuntimeException("could not construct an email address", ae);
        }
    }

    public Iterable<Range<java.sql.Date>> getPeriodsOfService() {
        return Collections.singletonList(periodOfService);
    }

    public boolean isAdministrator() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT administrator FROM employee WHERE id = ?");
                try {
                    s.setInt(1, number);
                    ResultSet rs = s.executeQuery();
                    try {
                        return rs.first() && rs.getBoolean("administrator");
                    } finally {
                        rs.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public void setAdministrator(boolean administrator) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE employee SET administrator = ? WHERE id = ?");
                try {
                    s.setBoolean(1, administrator);
                    s.setInt(2, number);
                    int rowCount = s.executeUpdate();
                    assert rowCount == 1;
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Iterable<Project> getRelatedProjects() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT pm.project " + "FROM project_managers AS pm " + "JOIN projects AS p ON pm.project = p.id " + "WHERE pm.employee = ? AND " + "p.active = TRUE " + "UNION " + "SELECT pm.project " + "FROM project_cost_managers AS pm " + "JOIN projects AS p ON pm.project = p.id " + "WHERE pm.employee = ? AND " + "p.active = TRUE " + "UNION " + "SELECT DISTINCT(p.id) AS project " + "FROM projects AS p " + "JOIN tasks AS t ON t.project = p.id " + "JOIN task_assignments AS ta ON ta.task=t.id " + "WHERE ta.employee = ? AND " + "p.active = TRUE");
                try {
                    s.setInt(1, number);
                    s.setInt(2, number);
                    s.setInt(3, number);
                    ResultSet rs = s.executeQuery();
                    try {
                        Collection<Project> c = new ArrayList<Project>();
                        while (rs.next()) {
                            int project_id = rs.getInt("project");
                            c.add(sarariman.getProjects().get(project_id));
                        }
                        return c;
                    } finally {
                        rs.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public boolean isApprover() {
        return sarariman.getApprovers().contains(this);
    }

    public boolean isInvoiceManager() {
        return sarariman.getInvoiceManagers().contains(this);
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public int getAge() {
        return DateUtils.yearsBetween(birthdate.toDateMidnight(), new Date());
    }

    public boolean active(java.sql.Date date) {
        return RangeUtilities.contains(getPeriodsOfService(), date);
    }

    public SortedSet<Employee> getReports() {
        Collection<Integer> reportIDs = sarariman.getOrganizationHierarchy().getReports(number);
        final Function<Integer, Employee> employeeIDToEmployee = new Function<Integer, Employee>() {
            public Employee apply(Integer f) {
                return directory.getByNumber().get(f);
            }

        };
        Collection<Employee> reports = Collections2.transform(reportIDs, employeeIDToEmployee);
        Comparator<Employee> fullNameComparator = new Comparator<Employee>() {
            public int compare(Employee t, Employee t1) {
                return t.getFullName().compareTo(t1.getFullName());
            }

        };
        return ImmutableSortedSet.copyOf(fullNameComparator, reports);
    }

    public BigDecimal getDirectRate() {
        try {
            Connection connection = sarariman.openConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT rate FROM direct_rate WHERE employee = ? ORDER BY effective DESC LIMIT 1");
                try {
                    s.setInt(1, number);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        if (hasRow) {
                            return r.getBigDecimal("rate");
                        } else {
                            return null;
                        }
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal getDirectRate(java.sql.Date date) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT rate FROM direct_rate WHERE employee = ? AND effective <= ? ORDER BY effective DESC LIMIT 1");
                try {
                    s.setInt(1, number);
                    s.setDate(2, date);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        if (hasRow) {
                            return r.getBigDecimal("rate");
                        } else {
                            return null;
                        }
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<Task> getTasks() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT t.id FROM tasks AS t JOIN task_assignments AS a ON a.task = t.id LEFT OUTER JOIN projects AS p ON t.project = p.id LEFT OUTER JOIN customers AS c ON c.id = p.customer WHERE employee = ? AND t.active = TRUE AND (p.active = TRUE OR p.active IS NULL) AND (c.active = TRUE OR c.active IS NULL) ORDER BY t.billable, t.id");
                try {
                    ps.setInt(1, number);
                    ResultSet resultSet = ps.executeQuery();
                    try {
                        Collection<Task> list = new ArrayList<Task>();
                        while (resultSet.next()) {
                            int id = resultSet.getInt("id");
                            list.add(sarariman.getTasks().get(id));
                        }
                        return list;
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public String toString() {
        return "{" + fullName + "," + userName + "," + number + ",fulltime=" + fulltime + ",email=" + email + "}";
    }

    public BigDecimal getPaidTimeOff() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT SUM(amount) AS total " + "FROM paid_time_off " + "WHERE employee = ?");
                try {
                    s.setInt(1, number);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBigDecimal("total");
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal getRecentEntryLatency() {
        // FIXME: This needs to be parameterized and/or moved elsewhere
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT AVG(DATEDIFF(hours_changelog.timestamp, hours.date)) AS average " + "FROM hours " + "JOIN hours_changelog ON hours.employee = hours_changelog.employee AND hours.task = hours_changelog.task AND hours.date = hours_changelog.date " + "WHERE hours.employee = ? AND hours.date > DATE_SUB(NOW(), INTERVAL 7 DAY)");
                try {
                    s.setInt(1, number);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBigDecimal("average");
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<Employee> getAdministrativeAssistants() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT assistant " + "FROM individual_administrative_assistants " + "WHERE employee = ?");
                ps.setInt(1, number);
                try {
                    ResultSet resultSet = ps.executeQuery();
                    try {
                        ImmutableList.Builder<Employee> listBuilder = ImmutableList.<Employee>builder();
                        while (resultSet.next()) {
                            int task_id = resultSet.getInt("assistant");
                            listBuilder.add(directory.getByNumber().get(task_id));
                        }
                        return listBuilder.build();
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public URI getURI() {
        return URI.create(String.format("%semployee?id=%d", sarariman.getMountPoint(), number));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StackFrameEmployee other = (StackFrameEmployee)obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.number;
        return hash;
    }

}
