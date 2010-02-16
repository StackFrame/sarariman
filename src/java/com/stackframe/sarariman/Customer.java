/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class Customer {

    private final long id;
    private final String name;
    private final Sarariman sarariman;

    public static Map<Long, Customer> getCustomers(Sarariman sarariman) throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM customers ORDER BY name");
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Map<Long, Customer> map = new LinkedHashMap<Long, Customer>();
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    map.put(id, new Customer(sarariman, id, name));
                }
                return map;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    private Customer(Sarariman sarariman, long id, String name) {
        this.sarariman = sarariman;
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void update(String name) throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE customers SET name=? WHERE id=?");
        try {
            ps.setString(1, name);
            ps.setLong(2, id);
            ps.executeUpdate();
        } finally {
            ps.close();
            connection.close();
        }
    }

    public static Customer create(Sarariman sarariman, String name) throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO customers (name) VALUES(?)");
        try {
            ps.setString(1, name);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            try {
                rs.next();
                return new Customer(sarariman, rs.getLong(1), name);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    public void delete() throws SQLException {
        Connection connection = sarariman.openConnection();
        PreparedStatement ps = connection.prepareStatement("DELETE FROM customers WHERE id=?");
        try {
            ps.setLong(1, id);
            ps.executeUpdate();
        } finally {
            ps.close();
            connection.close();
        }
    }

    @Override
    public String toString() {
        return "{id=" + id + ",name=" + name + "}";
    }

}
