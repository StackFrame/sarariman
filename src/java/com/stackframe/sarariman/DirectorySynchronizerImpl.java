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
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
class DirectorySynchronizerImpl implements DirectorySynchronizer {

    private Set<Integer> getEmployeeIDs(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
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

    private void add(Employee employee, DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
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

    public void synchronize(Directory directory, DataSource dataSource) throws Exception {
        Set<Integer> employeeIDs = getEmployeeIDs(dataSource);
        for (Employee employee : directory.getByUserName().values()) {
            if (!employeeIDs.contains(employee.getNumber())) {
                // FIXME: Log this.
                System.err.println("Adding employee " + employee.getDisplayName());
                add(employee, dataSource);
            }
        }
    }

}
