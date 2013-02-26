/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectFundingAudit implements Audit {

    private final int project;
    private final DataSource dataSource;

    public ProjectFundingAudit(int project, DataSource dataSource) {
        this.project = project;
        this.dataSource = dataSource;
    }

    public String getDisplayName() {
        return "Project Funding";
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        try {
            BigDecimal funded = Project.getFunded(project, dataSource);
            BigDecimal expended = Project.getExpended(project, dataSource);
            if (expended != null && funded != null && expended.compareTo(funded) > 0) {
                c.add(new AuditResult(AuditResultType.error, "expended exceeds funded"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return c;
    }

}
