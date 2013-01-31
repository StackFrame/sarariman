/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author mcculley
 */
public class OrgChartGlobalAudit implements Audit {

    private final Sarariman sarariman;

    public OrgChartGlobalAudit(Sarariman sarariman) {
        this.sarariman = sarariman;
    }

    public String getDisplayName() {
        return "Organization Hierarchy";
    }

    private static Collection<Integer> employeesInOrgChart(Collection<OrganizationHierarchy.Node> nodes) {
        Collection<Integer> c = new HashSet<Integer>();
        for (OrganizationHierarchy.Node node : nodes) {
            c.add(node.id());
            c.addAll(employeesInOrgChart(node.directReports()));
        }

        return c;
    }

    private Collection<AuditResult> checkGlobal() {
        ImmutableList.Builder<AuditResult> listBuilder = ImmutableList.<AuditResult>builder();
        Collection<Employee> activeFulltimeEmployees = Collections2.filter(sarariman.getDirectory().getByUserName().values(), Utilities.activeFulltime);
        OrganizationHierarchy organizationHierarchy = sarariman.getOrganizationHierarchy();
        Collection<Integer> employeesInOrgChart = employeesInOrgChart(organizationHierarchy.getOrgChart());
        for (Employee employee : activeFulltimeEmployees) {
            if (!employeesInOrgChart.contains(employee.getNumber())) {
                listBuilder.add(new AuditResult(AuditResultType.error, String.format("%s is not in org chart", employee.getDisplayName())));
            }
        }

        return listBuilder.build();
    }

    public Collection<AuditResult> getResults() {
        return ImmutableList.copyOf(checkGlobal());
    }

}
