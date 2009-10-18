/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class Administrators {

    private final Sarariman sarariman;

    Administrators(Sarariman sarariman) {
        this.sarariman = sarariman;
    }

    public Collection<Employee> getAdministrators() throws SQLException {
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

    public void add(Employee employee) throws SQLException {
        PreparedStatement ps = sarariman.getConnection().prepareStatement("INSERT INTO administrators (employee) VALUES(?)");
        ps.setLong(1, employee.getNumber());
        ps.executeUpdate();
    }

    public void remove(Employee employee) throws SQLException {
        PreparedStatement ps = sarariman.getConnection().prepareStatement("DELETE FROM administrators WHERE employee=?");
        ps.setLong(1, employee.getNumber());
        ps.executeUpdate();
    }

}
