/*
 * Copyright (C) 2009-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stackframe.collect.RangeUtilities;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntry;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.taskassignments.TaskAssignment;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tickets.Ticket;
import com.stackframe.sarariman.timesheets.Timesheet;
import com.stackframe.sarariman.vacation.VacationEntry;
import com.stackframe.sarariman.vcard.vCardSource;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private final String givenName;

    private final String surname;

    private final String userName;

    private final int number;

    private final boolean fulltime;

    private final boolean active;

    private final String email;

    private final LocalDate birthdate;

    private final String displayName;

    private final Range<java.sql.Date> periodOfService;

    private final byte[] photo;

    private final LDAPDirectory directory;

    private final DataSource dataSource;

    private final Sarariman sarariman;

    private final String mobile;

    private final Iterable<URL> profileLinks;

    private final Iterable<String> titles;

    StackFrameEmployee(String fullName, String givenName, String surname, String userName, int number, boolean fulltime,
                       boolean active, String email, LocalDate birthdate, String displayName, Range<java.sql.Date> periodOfService,
                       byte[] photo, LDAPDirectory directory, DataSource dataSource, Sarariman sarariman, String mobile,
                       Iterable<URL> profileLinks, Iterable<String> titles) {
        this.directory = directory;
        this.fullName = fullName;
        this.givenName = givenName;
        this.surname = surname;
        this.userName = userName;
        this.number = number;
        this.fulltime = fulltime;
        this.active = active;
        this.email = email;
        this.birthdate = birthdate;
        this.displayName = displayName;
        this.periodOfService = periodOfService;
        this.photo = photo;
        this.dataSource = dataSource;
        this.sarariman = sarariman;
        this.mobile = mobile;
        this.profileLinks = profileLinks;
        this.titles = titles;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public boolean isFulltime() {
        return fulltime;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public InternetAddress getEmail() {
        try {
            return new InternetAddress(email, true);
        } catch (AddressException ae) {
            throw new RuntimeException("could not construct an email address", ae);
        }
    }

    @Override
    public Iterable<Range<java.sql.Date>> getPeriodsOfService() {
        return Collections.singletonList(periodOfService);
    }

    @Override
    public boolean isAdministrator() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT administrator FROM employee WHERE id = ?");) {
            s.setInt(1, number);
            try (ResultSet rs = s.executeQuery()) {
                return rs.first() && rs.getBoolean("administrator");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setAdministrator(boolean administrator) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE employee SET administrator = ? WHERE id = ?");) {
            s.setBoolean(1, administrator);
            s.setInt(2, number);
            int rowCount = s.executeUpdate();
            assert rowCount == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Set<Project> getCurrentlyAssignedProjects() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement(
                     "SELECT DISTINCT(p.id) AS project " +
                     "FROM projects AS p " +
                     "JOIN tasks AS t ON t.project = p.id " +
                     "JOIN task_assignments AS ta ON ta.task = t.id " +
                     "WHERE ta.employee = ? AND " +
                     "p.active = TRUE ")) {
            s.setInt(1, number);
            try (ResultSet rs = s.executeQuery();) {
                Set<Project> c = new HashSet<>();
                while (rs.next()) {
                    int project_id = rs.getInt("project");
                    c.add(sarariman.getProjects().get(project_id));
                }
                return c;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Set<Project> getProjectsAdministrativelyAssisting() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement(
                     "SELECT project " +
                     "FROM project_administrative_assistants " +
                     "WHERE assistant = ?");) {
            s.setInt(1, number);
            try (ResultSet rs = s.executeQuery();) {
                Set<Project> c = new HashSet<>();
                while (rs.next()) {
                    int project_id = rs.getInt("project");
                    c.add(sarariman.getProjects().get(project_id));
                }
                return c;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Iterable<Project> getRelatedProjects() {
        try (Connection connection = dataSource.getConnection();
             // FIXME: This triggers the slow query log on MySQL for no good reason that I can figure out.
             PreparedStatement s = connection.prepareStatement(
                     "SELECT pm.project " +
                     "FROM project_managers AS pm " +
                     "JOIN projects AS p ON pm.project = p.id " +
                     "WHERE pm.employee = @employee AND " +
                     "p.active = TRUE " +
                     "UNION " +
                     "SELECT pm.project " +
                     "FROM project_cost_managers AS pm " +
                     "JOIN projects AS p ON pm.project = p.id " +
                     "WHERE pm.employee = @employee AND " +
                     "p.active = TRUE " +
                     "UNION " +
                     "SELECT DISTINCT(p.id) AS project " +
                     "FROM projects AS p " +
                     "JOIN tasks AS t ON t.project = p.id " +
                     "JOIN task_assignments AS ta ON ta.task=t.id " +
                     "WHERE ta.employee = @employee AND " +
                     "p.active = TRUE " +
                     "UNION " +
                     "SELECT project FROM project_administrative_assistants WHERE assistant = @employee");) {
            s.execute(String.format("SET @employee = %d", number));
            try (ResultSet rs = s.executeQuery();) {
                Collection<Project> c = new ArrayList<>();
                while (rs.next()) {
                    int project_id = rs.getInt("project");
                    c.add(sarariman.getProjects().get(project_id));
                }
                return c;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public boolean isApprover() {
        return sarariman.getApprovers().contains(this);
    }

    @Override
    public boolean isInvoiceManager() {
        return sarariman.getInvoiceManagers().contains(this);
    }

    @Override
    public LocalDate getBirthdate() {
        return birthdate;
    }

    @Override
    public int getAge() {
        return DateUtils.yearsBetween(birthdate.toDateMidnight(), new Date());
    }

    @Override
    public PhoneNumber getMobile() {
        if (mobile == null) {
            return null;
        } else {
            try {
                return PhoneNumberUtil.getInstance().parse(mobile, "US");
            } catch (NumberParseException npe) {
                throw new IllegalArgumentException(npe);
            }
        }
    }

    @Override
    public boolean active(java.sql.Date date) {
        return RangeUtilities.contains(getPeriodsOfService(), date);
    }

    @Override
    public SortedSet<Employee> getReports() {
        Collection<Integer> reportIDs = sarariman.getOrganizationHierarchy().getReports(number);
        final Function<Integer, Employee> employeeIDToEmployee = (Integer f) -> directory.getByNumber().get(f);
        Collection<Employee> reports = Collections2.transform(reportIDs, employeeIDToEmployee);
        Comparator<Employee> fullNameComparator = (Employee t, Employee t1) -> t.getFullName().compareTo(t1.getFullName());
        return ImmutableSortedSet.copyOf(fullNameComparator, reports);
    }

    @Override
    public BigDecimal getDirectRate() {
        try (Connection connection = sarariman.openConnection();
             PreparedStatement s = connection.prepareStatement("SELECT rate FROM direct_rate WHERE employee = ? ORDER BY effective DESC LIMIT 1");) {
            s.setInt(1, number);
            try (ResultSet r = s.executeQuery();) {
                boolean hasRow = r.first();
                if (hasRow) {
                    return r.getBigDecimal("rate");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getDirectRate(java.sql.Date date) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT rate FROM direct_rate WHERE employee = ? AND effective <= ? ORDER BY effective DESC LIMIT 1");) {
            s.setInt(1, number);
            s.setDate(2, date);
            try (ResultSet r = s.executeQuery();) {
                boolean hasRow = r.first();
                if (hasRow) {
                    return r.getBigDecimal("rate");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Task> getDefaultTasks() {
        Collection<Task> tasks = new ArrayList<>();
        sarariman.getDefaultTaskAssignments().getAll().stream().filter((a) -> (fulltime || !a.isFullTimeOnly())).forEach((a) -> {
            tasks.add(a.getTask());
        });

        return tasks;
    }

    @Override
    public Iterable<Task> getTasks() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT t.id FROM tasks AS t " +
                     "JOIN task_assignments AS a ON a.task = t.id " +
                     "LEFT OUTER JOIN projects AS p ON t.project = p.id " +
                     "LEFT OUTER JOIN customers AS c ON c.id = p.customer " +
                     "WHERE employee = ? AND t.active = TRUE AND " +
                     "(p.active = TRUE OR p.active IS NULL) AND " +
                     "(c.active = TRUE OR c.active IS NULL) ORDER BY t.billable, t.id");) {
            ps.setInt(1, number);
            try (ResultSet resultSet = ps.executeQuery();) {
                Collection<Task> list = new ArrayList<>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    list.add(sarariman.getTasks().get(id));
                }

                list.addAll(getDefaultTasks());
                return list;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Iterable<Task> getAssignedTasks() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT task FROM task_assignments WHERE employee = ?");) {
            ps.setInt(1, number);
            try (ResultSet resultSet = ps.executeQuery();) {
                Collection<Task> list = new ArrayList<>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("task");
                    list.add(sarariman.getTasks().get(id));
                }

                return list;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public String toString() {
        return "{" + fullName + "," + userName + "," + number + ",fulltime=" + fulltime + ",email=" + email + "}";
    }

    @Override
    public BigDecimal getPaidTimeOff() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT SUM(amount) AS total " + "FROM paid_time_off " + "WHERE employee = ?");) {
            s.setInt(1, number);
            try (ResultSet r = s.executeQuery();) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getBigDecimal("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getRecentEntryLatency() {
        // FIXME: This needs to be parameterized and/or moved elsewhere
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement(
                     "SELECT AVG(DATEDIFF(hours_changelog.timestamp, hours.date)) AS average " +
                     "FROM hours " +
                     "JOIN hours_changelog ON hours.employee = hours_changelog.employee AND " +
                     "hours.task = hours_changelog.task AND hours.date = hours_changelog.date " +
                     "WHERE hours.employee = ? AND hours.date > DATE_SUB(NOW(), INTERVAL 7 DAY)");) {
            s.setInt(1, number);
            try (ResultSet r = s.executeQuery();) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getBigDecimal("average");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Employee> getAdministrativeAssistants() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT assistant " + "FROM individual_administrative_assistants " + "WHERE employee = ?");) {
            ps.setInt(1, number);
            try (ResultSet resultSet = ps.executeQuery();) {
                ImmutableList.Builder<Employee> listBuilder = ImmutableList.<Employee>builder();
                while (resultSet.next()) {
                    int task_id = resultSet.getInt("assistant");
                    listBuilder.add(directory.getByNumber().get(task_id));
                }
                return listBuilder.build();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public URI getURI() {
        return URI.create(String.format("%sstaff/%s", sarariman.getMountPoint(), userName));
    }

    @Override
    public URL getPhotoURL() {
        try {
            return new URL(String.format("%sphoto?uid=%s", sarariman.getMountPoint(), userName));
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public Map<Week, Timesheet> getTimesheets() {
        ContiguousSet<Week> allWeeks = ContiguousSet.create(Range.<Week>all(), Week.discreteDomain);
        Function<Week, Timesheet> f = (Week f1) -> sarariman.getTimesheets().get(StackFrameEmployee.this, f1);
        return Maps.asMap(allWeeks, f);
    }

    @Override
    public Map<Task, TaskAssignment> getTaskAssignments() {
        Function<Task, TaskAssignment> f = (Task f1) -> sarariman.getTaskAssignments().get(StackFrameEmployee.this, f1);
        return Maps.asMap(sarariman.getTasks().getAll(), f);
    }

    @Override
    public Iterable<VacationEntry> getUpcomingVacation() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT id FROM vacation WHERE employee=? AND (begin >= DATE(NOW()) OR end >= DATE(NOW()))");) {
            s.setInt(1, number);
            try (ResultSet r = s.executeQuery();) {
                List<VacationEntry> l = new ArrayList<>();
                while (r.next()) {
                    int entryID = r.getInt("id");
                    l.add(sarariman.getVacations().get(entryID));
                }
                return l;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<OutOfOfficeEntry> getUpcomingOutOfOffice() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT id FROM out_of_office WHERE employee=? AND end >= DATE(NOW())");) {
            s.setInt(1, number);
            try (ResultSet r = s.executeQuery();) {
                List<OutOfOfficeEntry> l = new ArrayList<>();
                while (r.next()) {
                    int entryID = r.getInt("id");
                    l.add(sarariman.getOutOfOfficeEntries().get(entryID));
                }
                return l;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Ticket> getUnclosedTickets() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement s = c.prepareStatement(
                     "SELECT updated.ticket, updated.latest, ticket_status.status " +
                     "FROM (SELECT assigned.ticket, MAX(ticket_status.updated) AS latest " +
                     "FROM (SELECT ticket, SUM(assignment) AS sum " +
                     "FROM ticket_assignment WHERE assignee = ? GROUP BY ticket) AS assigned " +
                     "JOIN ticket_status ON ticket_status.ticket = assigned.ticket " +
                     "WHERE assigned.sum > 0 GROUP BY ticket) AS updated " +
                     "JOIN ticket_status ON updated.ticket = ticket_status.ticket AND updated.latest = ticket_status.updated " +
                     "WHERE ticket_status.status != 'closed'");) {
            s.setInt(1, number);
            try (ResultSet r = s.executeQuery();) {
                List<Ticket> l = new ArrayList<>();
                while (r.next()) {
                    int ticketID = r.getInt("ticket");
                    l.add(sarariman.getTickets().get(ticketID));
                }

                return l;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getMonthlyHealthInsurancePremium() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT p.premium AS premium FROM insurance_membership AS m JOIN insurance_premium AS p ON m.plan = p.plan AND m.coverage = p.coverage AND m.begin <= DATE(NOW()) and (m.end IS NULL OR m.end >= DATE(NOW())) AND employee=?");) {
            s.setInt(1, number);
            try (ResultSet r = s.executeQuery();) {
                boolean hasRow = r.first();
                if (!hasRow) {
                    return BigDecimal.ZERO;
                } else {
                    return r.getBigDecimal("premium");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPayrollAdministrator() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT payroll_administrator FROM employee WHERE id = ?");) {
            s.setInt(1, number);
            try (ResultSet rs = s.executeQuery();) {
                return rs.first() && rs.getBoolean("payroll_administrator");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setPayrollAdministrator(boolean payrollAdministrator) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE employee SET payroll_administrator = ? WHERE id = ?");) {
            s.setBoolean(1, payrollAdministrator);
            s.setInt(2, number);
            int rowCount = s.executeUpdate();
            assert rowCount == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public boolean isBenefitsAdministrator() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT benefits_administrator FROM employee WHERE id = ?");) {
            s.setInt(1, number);
            try (ResultSet rs = s.executeQuery();) {
                return rs.first() && rs.getBoolean("benefits_administrator");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setBenefitsAdministrator(boolean benefitsAdministrator) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE employee SET benefits_administrator = ? WHERE id = ?");) {
            s.setBoolean(1, benefitsAdministrator);
            s.setInt(2, number);
            int rowCount = s.executeUpdate();
            assert rowCount == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public byte[] getPhoto() {
        return photo;
    }

    @Override
    public Iterable<URL> getProfileLinks() {
        return profileLinks;
    }

    @Override
    public Iterable<String> getTitles() {
        return titles;
    }

    @Override
    public Object getPresence() {
        return sarariman.getXMPPServer().getPresence(userName + "@stackframe.com");
    }

    @Override
    public String getGivenName() {
        return givenName;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public vCardSource vCardSource() {
        return new vCardSource() {
            @Override
            public String getFamilyName() {
                return surname;
            }

            @Override
            public String getGivenName() {
                return givenName;
            }

            @Override
            public String getFullName() {
                return StackFrameEmployee.this.getFullName();
            }

            @Override
            public String getEmailAddress() {
                return email;
            }

            @Override
            public String getOrganization() {
                return "StackFrame, LLC";
            }

            @Override
            public PhoneNumber getMobile() {
                return StackFrameEmployee.this.getMobile();
            }

        };
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
        return this.number == other.number;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.number;
        return hash;
    }

}
