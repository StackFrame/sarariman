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
public class TicketImpl extends AbstractTicket {

    private final int id;
    private final Timestamp created;
    private final Employee employeeCreator;

    public TicketImpl(int id) throws SQLException, NoSuchTicketException {
        this.id = id;
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT created, employee_creator FROM ticket WHERE id = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    if (resultSet.first()) {
                        created = resultSet.getTimestamp("created");
                        int employeeCreatorID = resultSet.getInt("employee_creator");
                        if (resultSet.wasNull()) {
                            employeeCreator = null;
                        } else {
                            employeeCreator = getDirectory().getByNumber().get(employeeCreatorID);
                        }
                    } else {
                        throw new NoSuchTicketException(id);
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

    public int getId() {
        return id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public Employee getEmployeeCreator() {
        return employeeCreator;
    }

}
