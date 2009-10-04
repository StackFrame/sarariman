package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class Project {

    private final int id;
    private final String name;
    private final Customer customer;

    public static Project lookup(DataSource dataSource, int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT p.id AS project_id, p.name AS project_name, " +
                    "c.id AS customer_id, c.name AS customer_name " +
                    "FROM projects AS p " +
                    "JOIN customers AS c ON c.id = p.customer " +
                    "WHERE p.id = ?");
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            try {
                if (!resultSet.first()) {
                    return null;
                } else {
                    String project_name = resultSet.getString("project_name");
                    int customer_id = resultSet.getInt("customer_id");
                    String customer_name = resultSet.getString("customer_name");
                    return new Project(id, project_name, new Customer(customer_id, customer_name));
                }
            } finally {
                resultSet.close();
            }
        } finally {
            connection.close();
        }
    }

    Project(int id, String name, Customer customer) {
        this.id = id;
        this.name = name;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Customer getCustomer() {
        return customer;
    }

}
