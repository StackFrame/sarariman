/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author mcculley
 */
public class TicketBean extends AbstractTicket {

    private int id;
    private Timestamp created;
    private Employee employeeCreator;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) throws SQLException {
        this.id = id;
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT created, employee_creator FROM ticket WHERE id = ?");
            try {
                query.setInt(1, getId());
                ResultSet resultSet = query.executeQuery();
                try {
                    if (resultSet.first()) {
                        created = resultSet.getTimestamp("created");
                        int employeeCreatorID = resultSet.getInt("employee_creator");
                        System.err.println("employeeCreatorID="+employeeCreatorID);
                        if (resultSet.wasNull()) {
                            employeeCreator = null;
                        } else {
                            employeeCreator = getDirectory().getByNumber().get(employeeCreatorID);
                        }
                    } else {
                        throw new SQLException("no such ticket");
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                query.close();
            }
        } finally {
            connection.close();
        }
    }

    public Timestamp getCreated() throws SQLException {
        return created;
    }

    public Employee getEmployeeCreator() throws SQLException {
        return employeeCreator;
    }

}
