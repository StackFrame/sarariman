/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
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

    private final long id;
    private final String name;

    public static Map<Long, Customer> getCustomers(Sarariman sarariman) throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM customers");
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Map<Long, Customer> map = new HashMap<Long, Customer>();
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    map.put(id, new Customer(id, name));
                }
                return map;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
        }
    }

    Customer(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
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
