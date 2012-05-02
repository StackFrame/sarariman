/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public class OrganizationHierarchyImpl implements OrganizationHierarchy {

    private final ConnectionFactory connectionFactory;

    public OrganizationHierarchyImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Collection<Integer> getManagers(int employee, Date date) {
        Collection<Integer> managers = new ArrayList<Integer>();
        try {
            Connection connection = connectionFactory.openConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT manager FROM organization_hierarchy WHERE employee=? AND ((begin <= ? AND end >= ?) OR (begin <= ? AND end IS NULL));");
                try {
                    ps.setInt(1, employee);
                    ps.setDate(2, date);
                    ps.setDate(3, date);
                    ps.setDate(4, date);
                    ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            managers.add(rs.getInt("manager"));
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return managers;
    }

    public Collection<Integer> getManagers(int employee) {
        return getManagers(employee, new Date(new java.util.Date().getTime()));
    }

}
