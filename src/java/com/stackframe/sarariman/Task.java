package com.stackframe.sarariman;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public static Collection<Task> getTasks() throws Exception {
        Context context = new InitialContext();
        DataSource dataSource = (DataSource)context.lookup("jdbc/sarariman");
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT t.id AS task_id, t.name AS task_name, t.billable, t.active, " +
                "p.id AS project_id, p.name AS project_name, " +
                "c.id AS customer_id, c.name AS customer_name " +
                "FROM tasks AS t " +
                "LEFT OUTER JOIN projects AS p ON t.project = p.id " +
                "LEFT OUTER JOIN customers AS c ON c.id = p.customer");
        ResultSet resultSet = ps.executeQuery();
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
                String customer_name = resultSet.getString("customer_name");
                project = new Project(project_id, project_name, new Customer(customer_id, customer_name));
            }

            list.add(new Task(id, task_name, billable, active, project));
        }

        resultSet.close();
        return list;
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