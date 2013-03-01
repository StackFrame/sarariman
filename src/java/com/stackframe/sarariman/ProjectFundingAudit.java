/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.projects.ProjectImpl;
import java.math.BigDecimal;
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
    private final OrganizationHierarchy organizationHierarchy;
    private final Directory directory;

    public ProjectFundingAudit(int project, DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory) {
        this.project = project;
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
    }

    public String getDisplayName() {
        return "Project Funding";
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        Project p = new ProjectImpl(project, dataSource, organizationHierarchy, directory);
        BigDecimal funded = p.getFunded();
        BigDecimal expended = p.getExpended();
        if (expended != null && funded != null && funded.compareTo(BigDecimal.ZERO) > 0 && expended.compareTo(funded) > 0) {
            c.add(new AuditResult(AuditResultType.error, "expended exceeds funded"));
        }

        return c;
    }

}
