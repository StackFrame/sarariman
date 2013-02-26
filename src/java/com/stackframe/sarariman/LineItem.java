/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LineItem {

    private final long id;
    private final String description;
    private final long project;
    private final BigDecimal funded;
    private final PeriodOfPerformance pop;
    private final DataSource dataSource;

    public static Collection<LineItem> getLineItems(DataSource dataSource, long project) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM line_items WHERE project=? ORDER BY id");
            ps.setLong(1, project);
            try {
                ResultSet resultSet = ps.executeQuery();
                try {
                    Collection<LineItem> lineItems = new ArrayList<LineItem>();
                    while (resultSet.next()) {
                        long id = resultSet.getLong("id");
                        String description = resultSet.getString("description");
                        BigDecimal funded = resultSet.getBigDecimal("funded");
                        PeriodOfPerformance pop = new PeriodOfPerformance(resultSet.getDate("pop_start"), resultSet.getDate("pop_end"));
                        lineItems.add(new LineItem(dataSource, id, description, project, funded, pop));
                    }

                    return lineItems;
                } finally {
                    resultSet.close();
                }
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    LineItem(DataSource dataSource, long id, String description, long project, BigDecimal funded, PeriodOfPerformance pop) {
        this.dataSource = dataSource;
        this.id = id;
        this.description = description;
        this.project = project;
        this.funded = funded;
        this.pop = pop;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getProject() {
        return project;
    }

    public BigDecimal getFunded() {
        return funded;
    }

    public PeriodOfPerformance getPop() {
        return pop;
    }

    public static LineItem create(DataSource dataSource, Long id, String description, Long project, Date pop_start, Date pop_end, BigDecimal funded) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO line_items (id, description, project, pop_start, pop_end, funded) VALUES(?, ?, ?, ?, ?, ?)");
            try {
                ps.setLong(1, id);
                ps.setString(2, description);
                ps.setLong(3, project);
                ps.setDate(4, pop_start);
                ps.setDate(5, pop_end);
                ps.setBigDecimal(6, funded);
                ps.executeUpdate();
                PeriodOfPerformance pop = new PeriodOfPerformance(pop_start, pop_end);
                return new LineItem(dataSource, id, description, project, funded, pop);
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    public void update(String description, Long project, Date pop_start, Date pop_end, BigDecimal funded) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE line_items SET description=?, project=?, pop_start=?, pop_end=?, funded=? WHERE id=?");
            try {
                ps.setString(1, description);
                ps.setLong(2, project);
                ps.setDate(3, pop_start);
                ps.setDate(4, pop_end);
                ps.setBigDecimal(5, funded);
                ps.executeUpdate();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    public void delete() throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM line_items WHERE id=?");
            try {
                ps.setLong(1, id);
                ps.executeUpdate();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    @Override
    public String toString() {
        return "{id=" + id + ",description=" + description + ",project=" + project + ",funded=" + funded + ",pop=" + pop + "}";
    }

}
