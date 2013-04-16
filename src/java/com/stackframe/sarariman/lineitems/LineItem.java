/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.lineitems;

import com.stackframe.sarariman.PeriodOfPerformance;
import java.math.BigDecimal;
import java.sql.Connection;
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

    public BigDecimal getFunded() {
        return funded;
    }

    public PeriodOfPerformance getPop() {
        return pop;
    }

    @Override
    public String toString() {
        return "{id=" + id + ",description=" + description + ",project=" + project + ",funded=" + funded + ",pop=" + pop + "}";
    }

}
