/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.AbstractLinkable;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.Workdays;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import com.stackframe.sql.SQLUtilities;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectedExpensesImpl extends AbstractLinkable implements ProjectedExpenses {

    private final Project project;
    private final String servletPath;
    private final DataSource dataSource;
    private final Workdays workdays;
    private final Tasks tasks;
    private final Directory directory;

    ProjectedExpensesImpl(Project project, String servletPath, DataSource dataSource, Workdays workdays, Tasks tasks, Directory directory) {
        this.project = project;
        this.servletPath = servletPath;
        this.dataSource = dataSource;
        this.workdays = workdays;
        this.tasks = tasks;
        this.directory = directory;
    }

    public URI getURI() {
        return URI.create(String.format("%s?project=%d", servletPath, project.getId()));
    }

    public Project getProject() {
        return project;
    }

    public Collection<ProjectedExpense> getLabor(PeriodOfPerformance pop) {
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement(
                        "SELECT labor_category_assignments.employee AS employee, labor_categories.rate AS rate, " +
                        "labor_projection.task AS task, labor_projection.utilization AS utilization, " +
                        "labor_categories.pop_start AS category_start, labor_categories.pop_end AS category_end, " +
                        "labor_category_assignments.pop_start AS assignment_start, " +
                        "labor_category_assignments.pop_end AS assignment_end, " +
                        "labor_projection.pop_start AS projection_start, labor_projection.pop_end AS projection_end " +
                        "FROM labor_categories " +
                        "JOIN labor_category_assignments ON labor_categories.id = labor_category_assignments.labor_category " +
                        "JOIN labor_projection ON labor_projection.employee = labor_category_assignments.employee " +
                        "JOIN tasks ON labor_projection.task = tasks.id " +
                        "WHERE labor_categories.project = ? " +
                        "AND tasks.project = ? " +
                        "AND labor_categories.pop_start <= labor_category_assignments.pop_start " +
                        "AND labor_categories.pop_end >= labor_category_assignments.pop_start " +
                        "AND (labor_category_assignments.pop_start <= ? AND labor_category_assignments.pop_end >= ?) " +
                        "AND (labor_projection.pop_start <= ? AND labor_projection.pop_end >= ?)");
                try {
                    s.setInt(1, project.getId());
                    s.setInt(2, project.getId());
                    s.setDate(3, SQLUtilities.convert(pop.getEnd()));
                    s.setDate(4, SQLUtilities.convert(pop.getStart()));
                    s.setDate(5, SQLUtilities.convert(pop.getEnd()));
                    s.setDate(6, SQLUtilities.convert(pop.getStart()));
                    ResultSet r = s.executeQuery();
                    try {
                        Collection<ProjectedExpense> result = new ArrayList<ProjectedExpense>();
                        while (r.next()) {
                            int employeeNumber = r.getInt("employee");
                            Employee employee = directory.getByNumber().get(employeeNumber);
                            BigDecimal rate = r.getBigDecimal("rate");
                            int taskNumber = r.getInt("task");
                            Task task = tasks.get(taskNumber);
                            BigDecimal utilization = r.getBigDecimal("utilization");
                            Date categoryStart = r.getDate("category_start");
                            Date categoryEnd = r.getDate("category_end");
                            PeriodOfPerformance category = new PeriodOfPerformance(categoryStart, categoryEnd);
                            Date assignmentStart = r.getDate("assignment_start");
                            Date assignmentEnd = r.getDate("assignment_end");
                            PeriodOfPerformance assignment = new PeriodOfPerformance(assignmentStart, assignmentEnd);
                            Date projectionStart = r.getDate("projection_start");
                            Date projectionEnd = r.getDate("projection_end");
                            PeriodOfPerformance projection = new PeriodOfPerformance(projectionStart, projectionEnd);
                            PeriodOfPerformance span = pop.intersection(projection).intersection(assignment).intersection(category);
                            BigDecimal numWorkdays = BigDecimal.valueOf(workdays.getWorkdays(span).size());
                            BigDecimal hours = BigDecimal.valueOf(8.0).multiply(numWorkdays).multiply(utilization);
                            BigDecimal cost = hours.multiply(rate);
                            ProjectedExpense e = new ProjectedExpenseImpl(employee, span, task, cost, hours);
                            result.add(e);
                        }

                        return result;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
