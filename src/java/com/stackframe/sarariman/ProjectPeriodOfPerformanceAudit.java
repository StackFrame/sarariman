/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.projects.ProjectImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectPeriodOfPerformanceAudit implements Audit {

    private final int project;
    private final DataSource dataSource;
    private final OrganizationHierarchy organizationHierarchy;
    private final Directory directory;

    public ProjectPeriodOfPerformanceAudit(int project, DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory) {
        this.project = project;
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
    }

    public String getDisplayName() {
        return "Project Period of Performance";
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        Date now = new Date();
        Date PoPEnd = new ProjectImpl(project, dataSource, organizationHierarchy, directory).getPoP().getEnd();
        if (now.compareTo(PoPEnd) > 0) {
            c.add(new AuditResult(AuditResultType.error, "beyond period of performance"));
        }

        return c;
    }

}
