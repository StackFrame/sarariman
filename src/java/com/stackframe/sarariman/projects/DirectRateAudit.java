/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Audit;
import com.stackframe.sarariman.AuditResult;
import com.stackframe.sarariman.AuditResultType;
import static com.stackframe.sql.SQLUtilities.convert;
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
public class DirectRateAudit implements Audit {

    private final Project project;

    private final DataSource dataSource;

    public DirectRateAudit(Project project, DataSource dataSource) {
        this.project = project;
        this.dataSource = dataSource;
    }

    @Override
    public String getDisplayName() {
        return "Direct Rate";
    }

    private Collection<AuditResult> results() throws SQLException {
        Collection<AuditResult> c = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement s = connection.prepareStatement("SELECT h.employee AS employee, h.date AS date " +
                                                               "FROM hours AS h " +
                                                               "JOIN tasks AS t ON h.task = t.id " +
                                                               "JOIN projects AS p ON p.id = t.project " +
                                                               "LEFT JOIN direct_rate AS d ON " +
                                                               "(d.employee = h.employee AND h.date >= d.start AND " +
                                                               "(h.date <= d.end OR d.end IS NULL)) " +
                                                               "WHERE t.project = ? AND h.date >= ? AND h.date <= ? " +
                                                               "AND d.rate IS NULL")) {
            s.setInt(1, project.getId());
            s.setDate(2, convert(project.getPoP().getStart()));
            s.setDate(3, convert(project.getPoP().getEnd()));
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    c.add(new AuditResult(AuditResultType.error,
                                          String.format("employee %d is missing a direct rate for %s", r.getInt("employee"),
                                                        r.getDate("date")),
                                          project.getURL()));
                }
            }
        }

        return c;
    }

    @Override
    public Collection<AuditResult> getResults() {
        try {
            Collection<AuditResult> c = new ArrayList<>();
            c.addAll(results());
            return c;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
