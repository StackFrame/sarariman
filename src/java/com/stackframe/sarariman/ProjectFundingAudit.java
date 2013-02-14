/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public class ProjectFundingAudit implements Audit {

    private final int project;
    private final ConnectionFactory connectionFactory;

    public ProjectFundingAudit(int project, ConnectionFactory connectionFactory) {
        this.project = project;
        this.connectionFactory = connectionFactory;
    }

    public String getDisplayName() {
        return "Project Funding";
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        try {
            BigDecimal funded = Project.getFunded(project, connectionFactory);
            BigDecimal expended = Project.getExpended(project, connectionFactory);
            if (expended != null && funded != null && expended.compareTo(funded) > 0) {
                c.add(new AuditResult(AuditResultType.error, "expended exceeds funded"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return c;
    }

}
