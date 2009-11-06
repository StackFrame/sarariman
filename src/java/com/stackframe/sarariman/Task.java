/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public class Task {

    private final int id;
    private final String name;
    private final boolean billable;
    private final boolean active;
    private final Project project;

    public static Collection<Task> getTasks(Sarariman sarariman) throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT t.id AS task_id, t.name AS task_name, t.billable, t.active, " +
                "p.id AS project_id, p.name AS project_name, " +
                "c.id AS customer_id, c.name AS customer_name " +
                "FROM tasks AS t " +
                "LEFT OUTER JOIN projects AS p ON t.project = p.id " +
                "LEFT OUTER JOIN customers AS c ON c.id = p.customer");
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Collection<Task> list = new ArrayList<Task>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("task_id");
                    String task_name = resultSet.getString("task_name");
                    boolean billable = resultSet.getBoolean("billable");
                    boolean active = resultSet.getBoolean("active");
                    int project_id = resultSet.getInt("project_id");
                    Project project = null;
                    if (project_id != 0) {
                        String project_name = resultSet.getString("project_name");
                        int customer_id = resultSet.getInt("customer_id");
                        project = new Project(sarariman, project_id, project_name, customer_id);
                    }

                    list.add(new Task(id, task_name, billable, active, project));
                }

                return list;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
        }
    }

    public static Collection<Task> getTasks(Sarariman sarariman, Project project) throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT t.id AS task_id, t.name AS task_name, t.billable, t.active " +
                "FROM tasks AS t " +
                "WHERE t.project = ?");
        ps.setLong(1, project.getId());
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Collection<Task> list = new ArrayList<Task>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("task_id");
                    String task_name = resultSet.getString("task_name");
                    boolean billable = resultSet.getBoolean("billable");
                    boolean active = resultSet.getBoolean("active");
                    list.add(new Task(id, task_name, billable, active, project));
                }

                return list;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
        }
    }

    private Task(int id, String name, boolean billable, boolean active, Project project) {
        this.id = id;
        this.name = name;
        this.billable = billable;
        this.active = active;
        this.project = project;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isBillable() {
        return billable;
    }

    public boolean isActive() {
        return active;
    }

    public Project getProject() {
        return project;
    }

}
