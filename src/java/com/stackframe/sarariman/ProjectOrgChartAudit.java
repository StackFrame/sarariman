/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.projects.Project;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectOrgChartAudit implements Audit {

    private final Project project;
    private final DataSource dataSource;
    private final OrganizationHierarchy organizationHierarchy;
    private final Directory directory;

    public ProjectOrgChartAudit(Project project, DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory) {
        this.project = project;
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
    }

    public String getDisplayName() {
        return "Project Organization Hierarchy";
    }

    public Collection<AuditResult> getResults() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement p = connection.prepareStatement(
                        "SELECT project_managers.employee AS manager, hours.employee, hours.date " +
                        "FROM hours " +
                        "JOIN tasks ON hours.task = tasks.id " +
                        "JOIN project_managers ON project_managers.project = tasks.project " +
                        "WHERE hours.date >= DATE_SUB(NOW(), INTERVAL 7 DAY) AND tasks.project = ? " +
                        "AND project_managers.employee != hours.employee " +
                        "AND hours.employee NOT IN (SELECT employee FROM project_managers WHERE project = ?)");
                try {
                    p.setInt(1, project.getId());
                    p.setInt(2, project.getId());
                    ResultSet rs = p.executeQuery();
                    try {
                        Set<Integer> missing = new HashSet<Integer>();
                        while (rs.next()) {
                            int manager = rs.getInt("manager");
                            int employee = rs.getInt("employee");
                            Date date = rs.getDate("date");
                            Collection<Integer> orgChartManagers = organizationHierarchy.getManagers(employee, date);
                            if (orgChartManagers.contains(manager)) {
                                missing.remove(employee);
                            } else {
                                missing.add(employee);
                            }
                        }

                        Collection<AuditResult> results = new ArrayList<AuditResult>();
                        for (Integer employee : missing) {
                            String name = directory.getByNumber().get(employee).getDisplayName();
                            AuditResult result = new AuditResult(AuditResultType.error, String.format("%s is not in org chart under a manager of this project", name), project.getURL());
                            results.add(result);
                        }

                        return results;
                    } finally {
                        rs.close();
                    }
                } finally {
                    p.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
