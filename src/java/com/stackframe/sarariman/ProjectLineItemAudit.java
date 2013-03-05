/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.projects.Projects;
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
public class ProjectLineItemAudit implements Audit {

    private final int project;
    private final DataSource dataSource;
    private final Projects projects;

    public ProjectLineItemAudit(int project, DataSource dataSource, Projects projects) {
        this.project = project;
        this.dataSource = dataSource;
        this.projects = projects;
    }

    public String getDisplayName() {
        return "Project Line Item";
    }

    private Collection<Integer> tasksWithNoLineItem() throws SQLException {
        Collection<Integer> c = new ArrayList<Integer>();
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement s = connection.prepareStatement("SELECT id FROM tasks WHERE line_item IS NULL AND project = ?");
            try {
                s.setInt(1, project);
                ResultSet r = s.executeQuery();
                try {
                    while (r.next()) {
                        c.add(r.getInt("id"));
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

        return c;
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        try {
            Collection<LineItem> lineItems = projects.get(project).getLineItems();
            boolean hasLineItems = !lineItems.isEmpty();
            if (hasLineItems) {
                Collection<Integer> tasksWithNoLineItem = tasksWithNoLineItem();
                for (int task : tasksWithNoLineItem) {
                    c.add(new AuditResult(AuditResultType.error, String.format("task %d has no line item", task)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return c;
    }

}
