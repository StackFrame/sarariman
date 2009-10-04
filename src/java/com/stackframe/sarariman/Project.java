package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author mcculley
 */
public class Project {

    private final int id;
    private final String name;
    private final Customer customer;

    public static Project lookup(Sarariman sarariman, int id) throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "SELECT p.id AS project_id, p.name AS project_name, " +
                "c.id AS customer_id, c.name AS customer_name " +
                "FROM projects AS p " +
                "JOIN customers AS c ON c.id = p.customer " +
                "WHERE p.id = ?");
        try {
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
            ps.close();
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
