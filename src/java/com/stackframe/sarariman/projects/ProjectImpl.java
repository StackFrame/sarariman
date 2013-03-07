/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.google.common.collect.ImmutableList;
import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Audit;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.LineItem;
import com.stackframe.sarariman.OrganizationHierarchy;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.ProjectFundingAudit;
import com.stackframe.sarariman.ProjectLineItemAudit;
import com.stackframe.sarariman.ProjectOrgChartAudit;
import com.stackframe.sarariman.ProjectPeriodOfPerformanceAudit;
import com.stackframe.sarariman.clients.Client;
import com.stackframe.sarariman.clients.ClientImpl;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
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

    ProjectImpl(int id, DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory, Tasks tasks, Projects projects, String servletPath) {
        this.id = id;
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
        this.tasks = tasks;
        this.projects = projects;
        this.servletPath = servletPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT name FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("name");
                    } finally {
                        r.close();
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

    public void setName(String name) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET name = ? WHERE id = ?");
                try {
                    s.setString(1, name);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public String getContract() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT contract_number FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("contract_number");
                    } finally {
                        r.close();
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

    public void setContract(String contract) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET contract_number = ? WHERE id = ?");
                try {
                    s.setString(1, contract);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public String getSubcontract() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT subcontract_number FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("subcontract_number");
                    } finally {
                        r.close();
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

    public void setSubcontract(String subcontract) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET subcontract_number = ? WHERE id = ?");
                try {
                    s.setString(1, subcontract);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public String getInvoiceText() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT invoice_text FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("invoice_text");
                    } finally {
                        r.close();
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

    public void setInvoiceText(String text) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET invoice_text = ? WHERE id = ?");
                try {
                    s.setString(1, text);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public BigDecimal getFunded() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT funded FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBigDecimal("funded");
                    } finally {
                        r.close();
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

    public void setFunded(BigDecimal funded) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET funded = ? WHERE id = ?");
                try {
                    s.setBigDecimal(1, funded);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public BigDecimal getPreviouslyBilled() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT previously_billed FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBigDecimal("previously_billed");
                    } finally {
                        r.close();
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

    public void setPreviouslyBilled(BigDecimal previouslyBilled) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET previously_billed = ? WHERE id = ?");
                try {
                    s.setBigDecimal(1, previouslyBilled);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public BigDecimal getODCFee() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT odc_fee FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBigDecimal("odc_fee");
                    } finally {
                        r.close();
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

    public void setODCFee(BigDecimal fee) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET odc_fee = ? WHERE id = ?");
                try {
                    s.setBigDecimal(1, fee);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public boolean isManager(Employee employee) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM project_managers WHERE employee=? AND project=?");
                try {
                    ps.setInt(1, employee.getNumber());
                    ps.setLong(2, id);
                    ResultSet rs = ps.executeQuery();
                    try {
                        return rs.first();
                    } finally {
                        rs.close();
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

    public boolean isCostManager(Employee employee) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM project_cost_managers WHERE employee=? AND project=?");
                try {
                    ps.setInt(1, employee.getNumber());
                    ps.setLong(2, id);
                    ResultSet rs = ps.executeQuery();
                    try {
                        return rs.first();
                    } finally {
                        rs.close();
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

    public Client getClient() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT customer FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        int client_id = r.getInt("customer");
                        if (client_id == 0) {
                            return null;
                        } else {
                            return new ClientImpl(client_id, dataSource);
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
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public void setClient(Client client) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET customer = ? WHERE id = ?");
                try {
                    s.setInt(1, client.getId());
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public PeriodOfPerformance getPoP() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT pop_start, pop_end FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.next();
                        assert hasRow;
                        Date pop_start = r.getDate("pop_start");
                        Date pop_end = r.getDate("pop_end");
                        return new PeriodOfPerformance(pop_start, pop_end);
                    } finally {
                        r.close();
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

    public void setPoP(PeriodOfPerformance pop) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET pop_start = ?, pop_end = ? WHERE id = ?");
                try {
                    s.setDate(1, new Date(pop.getStart().getTime()));
                    s.setDate(2, new Date(pop.getEnd().getTime()));
                    s.setInt(3, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public int getTerms() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT terms FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getInt("terms");
                    } finally {
                        r.close();
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

    public void setTerms(int terms) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET terms = ? WHERE id = ?");
                try {
                    s.setInt(1, terms);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public boolean isActive() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT active FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBoolean("active");
                    } finally {
                        r.close();
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

    public void setActive(boolean active) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET active = ? WHERE id = ?");
                try {
                    s.setBoolean(1, active);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public Collection<Audit> getAudits() {
        Collection<Audit> c = new ArrayList<Audit>();
        c.add(new ProjectOrgChartAudit(id, dataSource, organizationHierarchy, directory));
        c.add(new ProjectPeriodOfPerformanceAudit(id, projects));
        c.add(new ProjectFundingAudit(id, projects));
        c.add(new ProjectLineItemAudit(id, dataSource, projects));
        return c;
    }

    public BigDecimal getExpended() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal "
                        + "FROM hours AS h "
                        + "JOIN tasks AS t on h.task = t.id "
                        + "JOIN projects AS p on p.id = t.project "
                        + "JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end) "
                        + "JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)"
                        + "WHERE t.project = ? AND t.billable = TRUE and h.duration > 0");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.next();
                        assert hasRow;
                        return r.getBigDecimal("costTotal");
                    } finally {
                        r.close();
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

    public Iterable<Date> getDaysBilled() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT(date) AS date "
                        + "FROM hours AS h "
                        + "JOIN tasks AS t on h.task = t.id "
                        + "JOIN projects AS p on p.id = t.project "
                        + "JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end) "
                        + "JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id) "
                        + "WHERE p.id = ? AND t.billable = TRUE;");
                try {
                    ps.setLong(1, id);
                    ResultSet rs = ps.executeQuery();
                    try {
                        Collection<Date> days = new TreeSet<Date>();
                        while (rs.next()) {
                            days.add(rs.getDate("date"));
                        }

                        return days;
                    } finally {
                        rs.close();
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

    public Collection<LineItem> getLineItems() {
        try {
            return LineItem.getLineItems(dataSource, id);
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Collection<Task> getTasks() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT t.id AS task_id "
                        + "FROM tasks AS t "
                        + "WHERE t.project = ?");
                ps.setInt(1, id);
                try {
                    ResultSet resultSet = ps.executeQuery();
                    try {
                        Collection<Task> list = new ArrayList<Task>();
                        while (resultSet.next()) {
                            int task_id = resultSet.getInt("task_id");
                            list.add(tasks.get(task_id));
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

    public Iterable<Employee> getAdministrativeAssistants() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT assistant "
                        + "FROM project_administrative_assistants "
                        + "WHERE project = ?");
                ps.setInt(1, id);
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
        return URI.create(String.format("%s?id=%d", servletPath, id));
    }

    public void delete() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM projects WHERE id=?");
                try {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
