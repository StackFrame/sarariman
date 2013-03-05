/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.projects.Projects;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public class ProjectFundingAudit implements Audit {

    private final int project;
    private final Projects projects;

    public ProjectFundingAudit(int project, Projects projects) {
        this.project = project;
        this.projects = projects;
    }

    public String getDisplayName() {
        return "Project Funding";
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        Project p = projects.get(project);
        BigDecimal funded = p.getFunded();
        BigDecimal expended = p.getExpended();
        if (expended != null && funded != null && funded.compareTo(BigDecimal.ZERO) > 0 && expended.compareTo(funded) > 0) {
            c.add(new AuditResult(AuditResultType.error, "expended exceeds funded"));
        }

        return c;
    }

}
