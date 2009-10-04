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
public class Customer {

    private final int id;
    private final String name;

    public static Map<Integer, Customer> getCustomers(Sarariman sarariman) throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM customers");
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Map<Integer, Customer> map = new HashMap<Integer, Customer>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    System.err.println("got name=" + name);
                    map.put(id, new Customer(id, name));
                }
                System.err.println("map=" + map);
                return map;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
        }
    }

    Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{id=" + id + ",name=" + name + "}";
    }

}
