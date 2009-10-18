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
public class Administrators extends AbstractCollection<Employee> {

    private final Sarariman sarariman;

    Administrators(Sarariman sarariman) {
        this.sarariman = sarariman;
    }

    private Collection<Employee> getAdministrators() throws SQLException {
        Connection connection = sarariman.getConnection();
        Map<Object, Employee> employees = sarariman.getDirectory().getByNumber();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM administrators");
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
        }
    }

    public Iterator<Employee> iterator() {
        try {
            final Iterator<Employee> administrators = getAdministrators().iterator();
            return new Iterator<Employee>() {

                public boolean hasNext() {
                    return administrators.hasNext();
                }

                public Employee next() {
                    return administrators.next();
                }

                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        Connection connection = sarariman.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as numrows FROM administrators");
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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean add(Employee employee) {
        try {
            PreparedStatement ps = sarariman.getConnection().prepareStatement("INSERT INTO administrators (employee) VALUES(?)");
            ps.setLong(1, employee.getNumber());
            ps.executeUpdate();
            ps.close();
            return true;
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
            PreparedStatement ps = sarariman.getConnection().prepareStatement("DELETE FROM administrators WHERE employee=?");
            ps.setLong(1, employee.getNumber());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
