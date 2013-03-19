/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.projects.Projects;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import javax.sql.DataSource;

/**
 * An Audit which looks for active projects that have no cost managers.
 *
 * @author mcculley
 */
public class ProjectCostManagerGlobalAudit implements Audit {

    private final DataSource dataSource;
    private final Projects projects;

    public ProjectCostManagerGlobalAudit(DataSource dataSource, Projects projects) {
        this.dataSource = dataSource;
        this.projects = projects;
    }

    public String getDisplayName() {
        return "Project Cost Managers";
    }

    public Collection<AuditResult> getResults() {
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement(
                        "SELECT p.id AS project " +
                        "FROM projects AS p " +
                        "LEFT OUTER JOIN project_cost_managers AS m ON m.project = p.id " +
                        "WHERE p.active = TRUE AND m.employee IS NULL");
                try {
                    ResultSet r = s.executeQuery();
                    try {
                        ImmutableList.Builder<AuditResult> listBuilder = ImmutableList.<AuditResult>builder();
                        while (r.next()) {
                            int project = r.getInt("project");
                            Project p = projects.get(project);
                            AuditResult result = new AuditResult(AuditResultType.error,
                                                                 String.format("project %d (%s) has no cost managers",
                                                                               project, p.getName()), p.getURL());
                            listBuilder.add(result);
                        }

                        return listBuilder.build();
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
