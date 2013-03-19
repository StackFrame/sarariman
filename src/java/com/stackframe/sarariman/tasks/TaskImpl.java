/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tasks;

import com.google.common.collect.ImmutableList;
import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.projects.Projects;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TaskImpl extends AbstractLinkable implements Task {

    private final int id;
    private final DataSource dataSource;
    private final String servletPath;
    private final Projects projects;
    private final Tasks tasks;

    TaskImpl(int id, DataSource dataSource, String servletPath, Projects projects, Tasks tasks) {
        this.id = id;
        this.dataSource = dataSource;
        this.servletPath = servletPath;
        this.projects = projects;
        this.tasks = tasks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT name FROM tasks WHERE id = ?");
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
                PreparedStatement s = connection.prepareStatement("UPDATE tasks SET name = ? WHERE id = ?");
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

    public boolean isBillable() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT billable FROM tasks WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getBoolean("billable");
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

    public void setBillable(boolean billable) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE tasks SET billable = ? WHERE id = ?");
                try {
                    s.setBoolean(1, billable);
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
                PreparedStatement s = connection.prepareStatement("SELECT active FROM tasks WHERE id = ?");
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
                PreparedStatement s = connection.prepareStatement("UPDATE tasks SET active = ? WHERE id = ?");
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

    public Project getProject() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT project FROM tasks WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        int project_id = r.getInt("project");
                        if (project_id == 0) {
                            return null;
                        } else {
                            return projects.get(project_id);
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

    public void setProject(Project project) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE tasks SET project = ? WHERE id = ?");
                try {
                    s.setInt(1, project.getId());
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

    public int getLineItem() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT line_item FROM tasks WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getInt("line_item");
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

    public void setLineItem(int lineItem) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE tasks SET line_item = ? WHERE id = ?");
                try {
                    s.setInt(1, lineItem);
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

    public String getDescription() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT description FROM tasks WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("description");
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

    public void setDescription(String description) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE tasks SET description = ? WHERE id = ?");
                try {
                    s.setString(1, description);
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

    public String getClientDesignation() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT client_designation FROM tasks WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("client_designation");
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

    public void setClientDesignation(String clientDesignation) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE tasks SET client_designation = ? WHERE id = ?");
                try {
                    s.setString(1, clientDesignation);
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

    public BigDecimal getExpendedLabor() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement(
                        "SELECT SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal " +
                        "FROM hours AS h " +
                        "JOIN tasks AS t on h.task = t.id " +
                        "JOIN projects AS p on p.id = t.project " +
                        "JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end) " +
                        "JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id) " +
                        "WHERE t.id=? AND t.billable=TRUE and h.duration > 0");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        BigDecimal costTotal = r.getBigDecimal("costTotal");
                        if (costTotal == null) {
                            costTotal = BigDecimal.ZERO;
                        }

                        for (Task child : getChildren()) {
                            costTotal = costTotal.add(child.getExpendedLabor());
                        }

                        return costTotal;
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

    public BigDecimal getExpendedOtherDirectCosts() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT SUM(cost) AS total FROM expenses WHERE task = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        BigDecimal total = r.getBigDecimal("total");
                        if (total == null) {
                            total = BigDecimal.ZERO;
                        }

                        for (Task child : getChildren()) {
                            total = total.add(child.getExpendedOtherDirectCosts());
                        }

                        return total;
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

    public BigDecimal getExpended() {
        return getExpendedLabor().add(getExpendedOtherDirectCosts());
    }

    public URI getURI() {
        return URI.create(String.format("%s?task_id=%d", servletPath, id));
    }

    public Collection<Task> getChildren() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT child FROM task_hierarchy WHERE parent = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        ImmutableList.Builder<Task> b = ImmutableList.<Task>builder();
                        while (r.next()) {
                            int task = r.getInt("child");
                            b.add(tasks.get(task));
                        }

                        return b.build();
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

    public Task getParent() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT parent FROM task_hierarchy WHERE child = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        if (hasRow) {
                            int task = r.getInt("parent");
                            assert !r.next();
                            return tasks.get(task);
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
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.id;
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
        final TaskImpl other = (TaskImpl)obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
