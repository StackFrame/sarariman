/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.projects.Projects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public class ProjectPeriodOfPerformanceAudit implements Audit {

    private final int project;
    private final Projects projects;

    public ProjectPeriodOfPerformanceAudit(int project, Projects projects) {
        this.project = project;
        this.projects = projects;
    }

    public String getDisplayName() {
        return "Project Period of Performance";
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        Date now = new Date();
        Date PoPEnd = projects.get(project).getPoP().getEnd();
        if (now.compareTo(PoPEnd) > 0) {
            c.add(new AuditResult(AuditResultType.error, "beyond period of performance"));
        }

        return c;
    }

}
