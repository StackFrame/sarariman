/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
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
    private final BigDecimal funded;
    private final Sarariman sarariman;

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
                    BigDecimal funded = resultSet.getBigDecimal("funded");
                    map.put(id, new Project(sarariman, id, name, customer, funded));
                }
                return map;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
        }
    }

    Project(Sarariman sarariman, long id, String name, long customer, BigDecimal funded) {
        this.sarariman = sarariman;
        this.id = id;
        this.name = name;
        this.customer = customer;
        this.funded = funded;
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

    public BigDecimal getFunded() {
        return funded;
    }

    public Collection<Task> getTasks() throws SQLException {
        return Task.getTasks(sarariman, this);
    }

    public static Project create(Sarariman sarariman, String name, Long customer, BigDecimal funded) throws SQLException {
        PreparedStatement ps = sarariman.getConnection().prepareStatement("INSERT INTO projects (name, customer, funded) VALUES(?, ?, ?)");
        try {
            ps.setString(1, name);
            ps.setLong(2, customer);
            ps.setBigDecimal(3, funded);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            try {
                rs.next();
                return new Project(sarariman, rs.getLong(1), name, customer, funded);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
    }

    public void update(String name, Long customer, BigDecimal funded) throws SQLException {
        PreparedStatement ps = sarariman.getConnection().prepareStatement("UPDATE projects SET name=?, customer=?, funded=? WHERE id=?");
        ps.setString(1, name);
        ps.setLong(2, customer);
        ps.setBigDecimal(3, funded);
        ps.setLong(4, id);
        ps.executeUpdate();
        ps.close();
    }

    public void delete() throws SQLException {
        PreparedStatement ps = sarariman.getConnection().prepareStatement("DELETE FROM projects WHERE id=?");
        ps.setLong(1, id);
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public String toString() {
        return "{id=" + id + ",name=" + name + ",customer=" + customer + ",funded=" + funded + "}";
    }

}
