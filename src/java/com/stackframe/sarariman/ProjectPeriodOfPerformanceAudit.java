/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public class ProjectPeriodOfPerformanceAudit implements Audit {

    private final int project;
    private final ConnectionFactory connectionFactory;

    public ProjectPeriodOfPerformanceAudit(int project, ConnectionFactory connectionFactory) {
        this.project = project;
        this.connectionFactory = connectionFactory;
    }

    public String getDisplayName() {
        return "Project Period of Performance";
    }

    private Date getPoPEnd() throws SQLException {
        Connection connection = connectionFactory.openConnection();
        try {
            PreparedStatement s = connection.prepareStatement("SELECT pop_end FROM projects WHERE id = ?");
            try {
                s.setInt(1, project);
                ResultSet r = s.executeQuery();
                try {
                    boolean hasRow = r.next();
                    assert hasRow;
                    return r.getDate("pop_end");
                } finally {
                    r.close();
                }
            } finally {
                s.close();
            }
        } finally {
            connection.close();
        }
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        Date now = new Date(System.currentTimeMillis());
        try {
            Date PoPEnd = getPoPEnd();
            if (now.compareTo(PoPEnd) > 0) {
                c.add(new AuditResult(AuditResultType.error, "beyond period of performance"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return c;
    }

}
