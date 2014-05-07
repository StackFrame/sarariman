/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Audit;
import com.stackframe.sarariman.DateUtils;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.EmailLogEntry;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.NamedResource;
import com.stackframe.sarariman.NamedResourceImpl;
import com.stackframe.sarariman.OrganizationHierarchy;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.ProjectFundingAudit;
import com.stackframe.sarariman.ProjectLineItemAudit;
import com.stackframe.sarariman.ProjectOrgChartAudit;
import com.stackframe.sarariman.ProjectPeriodOfPerformanceAudit;
import com.stackframe.sarariman.Week;
import com.stackframe.sarariman.Workdays;
import com.stackframe.sarariman.clients.Client;
import com.stackframe.sarariman.clients.Clients;
import com.stackframe.sarariman.lineitems.LineItem;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntries;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntry;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import static com.stackframe.sql.SQLUtilities.convert;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectImpl extends AbstractLinkable implements Project {

    private final int id;

    private final DataSource dataSource;

    private final OrganizationHierarchy organizationHierarchy;

    private final Directory directory;

    private final Tasks tasks;

    private final Projects projects;

    private final String servletPath;

    private final Clients clients;

    private final Workdays workdays;

    private final OutOfOfficeEntries oofEntries;

    private final LaborProjections laborProjections;

    private final String mountPoint;

    ProjectImpl(int id, DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory, Tasks tasks,
                Projects projects, String servletPath, Clients clients, Workdays workdays, OutOfOfficeEntries oofEntries,
                LaborProjections laborProjections, String mountPoint) {
        this.id = id;
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
        this.tasks = tasks;
        this.projects = projects;
        this.servletPath = servletPath;
        this.clients = clients;
        this.workdays = workdays;
        this.oofEntries = oofEntries;
        this.laborProjections = laborProjections;
        this.mountPoint = mountPoint;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT name FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getString("name");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setName(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET name = ? WHERE id = ?")) {
            s.setString(1, name);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public String getContract() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT contract_number FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getString("contract_number");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setContract(String contract) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET contract_number = ? WHERE id = ?")) {
            s.setString(1, contract);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public String getSubcontract() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT subcontract_number FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getString("subcontract_number");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setSubcontract(String subcontract) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET subcontract_number = ? WHERE id = ?")) {
            s.setString(1, subcontract);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public String getPurchaseOrder() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT purchase_order FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getString("purchase_order");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setPurchaseOrder(String purchaseOrder) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET purchase_order = ? WHERE id = ?")) {
            s.setString(1, purchaseOrder);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public String getInvoiceText() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT invoice_text FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getString("invoice_text");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setInvoiceText(String text) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET invoice_text = ? WHERE id = ?")) {
            s.setString(1, text);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public BigDecimal getFunded() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT funded FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getBigDecimal("funded");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setFunded(BigDecimal funded) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET funded = ? WHERE id = ?")) {
            s.setBigDecimal(1, funded);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public BigDecimal getPreviouslyBilled() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT previously_billed FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getBigDecimal("previously_billed");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setPreviouslyBilled(BigDecimal previouslyBilled) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET previously_billed = ? WHERE id = ?")) {
            s.setBigDecimal(1, previouslyBilled);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public BigDecimal getODCFee() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT odc_fee FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getBigDecimal("odc_fee");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setODCFee(BigDecimal fee) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET odc_fee = ? WHERE id = ?")) {
            s.setBigDecimal(1, fee);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public boolean isManager(Employee employee) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM project_managers WHERE employee=? AND project=?")) {
            ps.setInt(1, employee.getNumber());
            ps.setLong(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.first();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public boolean isCostManager(Employee employee) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM project_cost_managers " +
                                                                "WHERE employee=? AND project=?")) {
            ps.setInt(1, employee.getNumber());
            ps.setLong(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.first();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Client getClient() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT customer FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                int client_id = r.getInt("customer");
                if (client_id == 0) {
                    return null;
                } else {
                    return clients.get(client_id);
                }
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setClient(Client client) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET customer = ? WHERE id = ?")) {
            s.setInt(1, client.getId());
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public PeriodOfPerformance getPoP() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT pop_start, pop_end FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.next();
                assert hasRow;
                Date pop_start = r.getDate("pop_start");
                Date pop_end = r.getDate("pop_end");
                return new PeriodOfPerformance(pop_start, pop_end);
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setPoP(PeriodOfPerformance pop) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET pop_start = ?, pop_end = ? WHERE id = ?")) {
            s.setDate(1, convert(pop.getStart()));
            s.setDate(2, convert(pop.getEnd()));
            s.setInt(3, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public int getTerms() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT terms FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getInt("terms");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setTerms(int terms) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET terms = ? WHERE id = ?")) {
            s.setInt(1, terms);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public boolean isActive() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT active FROM projects WHERE id = ?")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.first();
                assert hasRow;
                return r.getBoolean("active");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public void setActive(boolean active) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("UPDATE projects SET active = ? WHERE id = ?")) {
            s.setBoolean(1, active);
            s.setInt(2, id);
            int numRows = s.executeUpdate();
            assert numRows == 1;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Collection<Audit> getAudits() {
        Collection<Audit> c = new ArrayList<>();
        c.add(new ProjectOrgChartAudit(this, dataSource, organizationHierarchy, directory));
        c.add(new ProjectPeriodOfPerformanceAudit(id, projects));
        c.add(new ProjectFundingAudit(id, projects));
        c.add(new ProjectLineItemAudit(id, dataSource, projects));
        c.add(new DirectRateAudit(this, dataSource));
        return c;
    }

    @Override
    public BigDecimal getExpended() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement(
                     "SELECT SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal " +
                     "FROM hours AS h " +
                     "JOIN tasks AS t on h.task = t.id " +
                     "JOIN projects AS p on p.id = t.project " +
                     "JOIN labor_category_assignments AS a " +
                     "ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end) " +
                     "JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start " +
                     "AND h.date <= c.pop_end AND c.project = p.id)" +
                     "WHERE t.project = ? AND t.billable = TRUE and h.duration > 0")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.next();
                assert hasRow;
                return r.getBigDecimal("costTotal");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Iterable<Date> getDaysBilled() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT(date) AS date " +
                                                                "FROM hours AS h " +
                                                                "JOIN tasks AS t on h.task = t.id " +
                                                                "JOIN projects AS p on p.id = t.project " +
                                                                "JOIN labor_category_assignments AS a ON " +
                                                                "(a.employee = h.employee AND h.date >= a.pop_start " +
                                                                "AND h.date <= a.pop_end) " +
                                                                "JOIN labor_categories AS c ON (c.id = a.labor_category " +
                                                                "AND h.date >= c.pop_start AND h.date <= c.pop_end " +
                                                                "AND c.project = p.id) " +
                                                                "WHERE p.id = ? AND t.billable = TRUE;");) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Collection<Date> days = new TreeSet<>();
                while (rs.next()) {
                    days.add(rs.getDate("date"));
                }

                return days;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Collection<LineItem> getLineItems() {
        try {
            return LineItem.getLineItems(dataSource, id);
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Collection<Task> getTasks() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT t.id AS task_id " +
                                                                "FROM tasks AS t " +
                                                                "WHERE t.project = ?");) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                Collection<Task> list = new ArrayList<>();
                while (resultSet.next()) {
                    int task_id = resultSet.getInt("task_id");
                    list.add(tasks.get(task_id));
                }

                return list;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Iterable<Employee> getAdministrativeAssistants() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT assistant " +
                                                                "FROM project_administrative_assistants " +
                                                                "WHERE project = ?");) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
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
    public Iterable<Employee> getManagers() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT employee " +
                                                                "FROM project_managers " +
                                                                "WHERE project = ?");) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                ImmutableList.Builder<Employee> listBuilder = ImmutableList.<Employee>builder();
                while (resultSet.next()) {
                    int task_id = resultSet.getInt("employee");
                    listBuilder.add(directory.getByNumber().get(task_id));
                }

                return listBuilder.build();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Iterable<Employee> getCostManagers() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT employee " +
                                                                "FROM project_cost_managers " +
                                                                "WHERE project = ?");) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                ImmutableList.Builder<Employee> listBuilder = ImmutableList.<Employee>builder();
                while (resultSet.next()) {
                    int task_id = resultSet.getInt("employee");
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
        return URI.create(String.format("%s?id=%d", servletPath, id));
    }

    private Collection<Date> getWorkedDates() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT(h.date) " +
                                                                "FROM hours AS h " +
                                                                "JOIN tasks AS t ON h.task = t.id " +
                                                                "JOIN projects AS p ON p.id = t.project " +
                                                                "WHERE project = ?");) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                ImmutableList.Builder<Date> listBuilder = ImmutableList.<Date>builder();
                while (resultSet.next()) {
                    Date date = resultSet.getDate("date");
                    listBuilder.add(date);
                }

                return listBuilder.build();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Iterable<Week> getWorkedWeeks() {
        return getWorkedDates().stream().map(DateUtils::week).collect(Collectors.toSet());
    }

    @Override
    public void delete() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM projects WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Employee> getDefaultCurrentlyAssigned() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT DISTINCT(ta.full_time_only) AS full_time_only " +
                                                               "FROM projects AS p " +
                                                               "JOIN tasks AS t ON t.project = p.id " +
                                                               "JOIN default_task_assignment AS ta ON ta.task = t.id " +
                                                               "WHERE p.id = ? AND " +
                                                               "p.active = TRUE")) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                ImmutableSet.Builder<Employee> b = ImmutableSet.builder();
                while (rs.next()) {
                    boolean fullTimeOnly = rs.getBoolean("full_time_only");
                    for (Employee e : directory.getEmployees()) {
                        if (e.isActive()) {
                            if (!fullTimeOnly || e.isFulltime()) {
                                b.add(e);
                            }
                        }
                    }
                }

                return b.build();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    private Set<Employee> getExplicitlyCurrentlyAssigned() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT DISTINCT(ta.employee) AS employee " +
                                                               "FROM projects AS p " +
                                                               "JOIN tasks AS t ON t.project = p.id " +
                                                               "JOIN task_assignments AS ta ON ta.task = t.id " +
                                                               "WHERE p.id = ? AND " +
                                                               "p.active = TRUE")) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                ImmutableSet.Builder<Employee> b = ImmutableSet.builder();
                while (rs.next()) {
                    int employee = rs.getInt("employee");
                    b.add(directory.getByNumber().get(employee));
                }

                return b.build();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Set<Employee> getCurrentlyAssigned() {
        return Sets.union(getExplicitlyCurrentlyAssigned(), getDefaultCurrentlyAssigned());
    }

    @Override
    public Collection<NamedResource> getResources() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT URL, description " +
                                                               "FROM project_links " +
                                                               "WHERE project = ?")) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                ImmutableList.Builder<NamedResource> b = ImmutableList.<NamedResource>builder();
                while (rs.next()) {
                    URL url = rs.getURL("URL");
                    String description = rs.getString("description");
                    try {
                        b.add(new NamedResourceImpl(url.toURI(), description));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }

                return b.build();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Collection<LaborProjection> getLaborProjections() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT labor_projection.id FROM labor_projection " +
                                                                "JOIN tasks ON labor_projection.task = tasks.id " +
                                                                "WHERE tasks.project = ?");) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                Collection<LaborProjection> list = new ArrayList<>();
                while (resultSet.next()) {
                    int laborProjectionId = resultSet.getInt("labor_projection.id");
                    list.add(laborProjections.get(laborProjectionId));
                }

                return list;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public Collection<EmailLogEntry> getEmailLogEntries(Week week) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT sender, sent " +
                                                                "FROM project_timesheet_email_log " +
                                                                "WHERE project = ? AND week = ?");) {
            ps.setInt(1, id);
            ps.setString(2, week.getName());
            try (ResultSet resultSet = ps.executeQuery()) {
                Collection<EmailLogEntry> list = new ArrayList<>();
                while (resultSet.next()) {
                    int senderNumber = resultSet.getInt("sender");
                    Employee sender = directory.getByNumber().get(senderNumber);
                    Date sent = resultSet.getDate("sent");
                    list.add(new EmailLogEntry(sender, sent));
                }

                return list;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public ProjectedExpenses getProjectedExpenses() {
        return new ProjectedExpensesImpl(this, mountPoint + "projection.jsp", dataSource, workdays, tasks, directory);
    }

    @Override
    public Iterable<OutOfOfficeEntry> getUpcomingOutOfOffice() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT oof.id " +
                                                      "FROM out_of_office AS oof " +
                                                      "JOIN task_assignments AS ta ON ta.employee = oof.employee " +
                                                      "JOIN tasks AS t on t.id = ta.task " +
                                                      "JOIN projects AS p ON p.id = t.project " +
                                                      "WHERE p.id = ? AND oof.end >= DATE(NOW()) " +
                                                      "GROUP BY oof.employee, oof.begin, oof.end, oof.comment " +
                                                      "ORDER BY oof.begin")) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                List<OutOfOfficeEntry> l = new ArrayList<>();
                while (r.next()) {
                    int entryID = r.getInt("id");
                    l.add(oofEntries.get(entryID));
                }

                return l;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getLaborHoursBilled(PeriodOfPerformance pop) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT SUM(h.duration) AS total " +
                                                               "FROM hours AS h " +
                                                               "JOIN tasks AS t on h.task = t.id " +
                                                               "JOIN projects AS p on p.id = t.project " +
                                                               "WHERE t.project = ? AND h.date >= ? AND h.date <= ?")) {
            s.setInt(1, id);
            s.setDate(2, new Date(pop.getStart().getTime()));
            s.setDate(3, new Date(pop.getEnd().getTime()));
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.next();
                assert hasRow;
                return r.getBigDecimal("total");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public BigDecimal getLaborDirectCosts(PeriodOfPerformance pop) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT SUM(TRUNCATE(d.rate * h.duration + 0.009, 2)) AS total " +
                                                               "FROM hours AS h " +
                                                               "JOIN tasks AS t on h.task = t.id " +
                                                               "JOIN projects AS p on p.id = t.project " +
                                                               "JOIN direct_rate AS d ON (d.employee = h.employee " +
                                                               "AND h.date >= d.start AND (h.date <= d.end OR d.end IS NULL)) " +
                                                               "WHERE t.project = ? AND h.date >= ? AND h.date <= ?")) {
            s.setInt(1, id);
            s.setDate(2, new Date(pop.getStart().getTime()));
            s.setDate(3, new Date(pop.getEnd().getTime()));
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.next();
                assert hasRow;
                return r.getBigDecimal("total");
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public BigDecimal getOtherDirectCosts(PeriodOfPerformance pop) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement(
                     "SELECT SUM(e.cost) AS total " +
                     "FROM expenses AS e " +
                     "JOIN tasks AS t on e.task = t.id " +
                     "JOIN projects AS p on p.id = t.project " +
                     "WHERE project=? AND date >= ? AND date <= ?")) {
            s.setInt(1, id);
            s.setDate(2, new Date(pop.getStart().getTime()));
            s.setDate(3, new Date(pop.getEnd().getTime()));
            try (ResultSet r = s.executeQuery()) {
                boolean hasRow = r.next();
                assert hasRow;
                BigDecimal total = r.getBigDecimal("total");
                return total == null ? BigDecimal.ZERO : total;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ProjectImpl other = (ProjectImpl)obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

}
