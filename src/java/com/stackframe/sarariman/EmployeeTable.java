/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class EmployeeTable extends AbstractCollection<Employee> {

    private final Sarariman sarariman;
    private final String tableName;

    EmployeeTable(Sarariman sarariman, String tableName) {
        this.sarariman = sarariman;
        this.tableName = tableName;
    }

    private Collection<Employee> getEmployees() throws SQLException {
        Connection connection = sarariman.openConnection();
        Map<Object, Employee> employees = sarariman.getDirectory().getByNumber();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName);
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Collection<Employee> result = new ArrayList<Employee>();
                while (resultSet.next()) {
                    result.add(employees.get(resultSet.getLong("employee")));
                }
                return result;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
            connection.close();
        }
    }

    public Iterator<Employee> iterator() {
        try {
            final Iterator<Employee> administrators = getEmployees().iterator();
            return new Iterator<Employee>() {

                Employee current;

                public boolean hasNext() {
                    return administrators.hasNext();
                }

                public Employee next() {
                    current = administrators.next();
                    return current;
                }

                public void remove() {
                    EmployeeTable.this.remove(current);
                }

            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as numrows FROM " + tableName);
            try {
                ResultSet resultSet = ps.executeQuery();
                try {
                    resultSet.next();
                    return resultSet.getInt("numrows");
                } finally {
                    resultSet.close();
                }
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean add(Employee employee) {
        try {
            Connection connection = sarariman.openConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO " + tableName + " (employee) VALUES(?)");
            try {
                ps.setLong(1, employee.getNumber());
                ps.executeUpdate();
                ps.close();
                return true;
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Employee)) {
            return false;
        }

        Employee employee = (Employee)o;
        try {
            Connection connection = sarariman.openConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableName + " WHERE employee=?");
            try {
                ps.setLong(1, employee.getNumber());
                ps.executeUpdate();
                ps.close();
                return true;
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
