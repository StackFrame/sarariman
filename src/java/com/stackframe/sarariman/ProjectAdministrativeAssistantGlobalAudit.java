/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.stackframe.sarariman.projects.Project;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import javax.sql.DataSource;

/**
 * An Audit which looks for active projects that have no administrative assistants.
 *
 * @author mcculley
 */
public class ProjectAdministrativeAssistantGlobalAudit implements Audit {

    private final Sarariman sarariman;

    public ProjectAdministrativeAssistantGlobalAudit(Sarariman sarariman) {
        this.sarariman = sarariman;
    }

    public String getDisplayName() {
        return "Project Administrative Assistant";
    }

    public Collection<AuditResult> getResults() {
        DataSource dataSource = sarariman.getDataSource();
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement(
                        "SELECT p.id AS project " +
                        "FROM projects AS p " +
                        "LEFT OUTER JOIN project_administrative_assistants AS a ON a.project = p.id " +
                        "WHERE p.active = TRUE AND a.assistant IS NULL;");
                try {
                    ResultSet r = s.executeQuery();
                    try {
                        ImmutableList.Builder<AuditResult> listBuilder = ImmutableList.<AuditResult>builder();
                        while (r.next()) {
                            int project = r.getInt("project");
                            Project p = sarariman.getProjects().get(project);
                            AuditResult auditResult = new AuditResult(AuditResultType.error, String.format("project %d has no administrative assistants", project), p.getURL());
                            listBuilder.add(auditResult);
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
