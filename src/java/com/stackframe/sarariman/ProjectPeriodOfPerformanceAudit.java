/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        Date now = new Date();
        try {
            Date PoPEnd = Project.getPoP(project, connectionFactory).getEnd();
            if (now.compareTo(PoPEnd) > 0) {
                c.add(new AuditResult(AuditResultType.error, "beyond period of performance"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return c;
    }

}
