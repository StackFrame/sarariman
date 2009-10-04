package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class Project {

    private final int id;
    private final String name;
    private final int customer;

    public static Map<Integer, Project> getProjects(Sarariman sarariman) throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM projects");
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Map<Integer, Project> map = new HashMap<Integer, Project>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int customer = resultSet.getInt("customer");
                    map.put(id, new Project(id, name, customer));
                }
                return map;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
        }
    }

    public static Project lookup(Sarariman sarariman, int id) throws SQLException {
        return sarariman.getProjects().get(id);
    }

    Project(int id, String name, int customer) {
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

    public int getCustomer() {
        return customer;
    }

}
