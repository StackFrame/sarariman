/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 *
 * @author mcculley
 */
class DirectorySynchronizerImpl implements DirectorySynchronizer {

    private Set<Integer> getEmployeeIDs(ConnectionFactory connectionFactory) throws SQLException {
        Connection connection = connectionFactory.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM employee");
            try {
                ResultSet rs = ps.executeQuery();
                ImmutableSet.Builder<Integer> setBuilder = ImmutableSet.builder();
                while (rs.next()) {
                    setBuilder.add(rs.getInt("id"));
                }

                return setBuilder.build();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    private void add(Employee employee, ConnectionFactory connectionFactory) throws SQLException {
        Connection connection = connectionFactory.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO employee(id) VALUES(?)");
            try {
                ps.setInt(1, employee.getNumber());
                int rowCount = ps.executeUpdate();
                assert rowCount == 1;
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    public void synchronize(Directory directory, ConnectionFactory connectionFactory) throws Exception {
        System.err.println("Synchronizing the database with the directory.");
        Set<Integer> employeeIDs = getEmployeeIDs(connectionFactory);
        for (Employee employee : directory.getByUserName().values()) {
            if (!employeeIDs.contains(employee.getNumber())) {
                System.err.println("Adding employee " + employee.getDisplayName());
                add(employee, connectionFactory);
            }
        }
    }

}
