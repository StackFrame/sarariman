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
public class Project {

    private final long id;
    private final String name;
    private final long customer;

    public static Map<Long, Project> getProjects(Sarariman sarariman) throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM projects ORDER BY name");
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Map<Long, Project> map = new LinkedHashMap<Long, Project>();
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    long customer = resultSet.getLong("customer");
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

    Project(long id, String name, long customer) {
        this.id = id;
        this.name = name;
        this.customer = customer;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCustomer() {
        return customer;
    }

}
