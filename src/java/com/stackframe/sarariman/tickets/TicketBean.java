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
    private Location creatorLocation;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) throws SQLException, NoSuchTicketException {
        this.id = id;
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT created, employee_creator, has_creator_location, creator_latitude, creator_longitude FROM ticket WHERE id = ?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    if (resultSet.first()) {
                        created = resultSet.getTimestamp("created");

                        int employeeCreatorID = resultSet.getInt("employee_creator");
                        if (!resultSet.wasNull()) {
                            employeeCreator = getDirectory().getByNumber().get(employeeCreatorID);
                        }

                        boolean hasCreatorLocation = resultSet.getBoolean("has_creator_location");
                        if (hasCreatorLocation) {
                            double latitude = resultSet.getDouble("creator_latitude");
                            double longitude = resultSet.getDouble("creator_longitude");
                            creatorLocation = new Location(latitude, longitude);
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

    public Timestamp getCreated() throws SQLException {
        return created;
    }

    public Employee getEmployeeCreator() throws SQLException {
        return employeeCreator;
    }

    public Location getCreatorLocation() {
        return creatorLocation;
    }

}
