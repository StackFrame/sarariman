/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Employee;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TicketImpl extends AbstractTicket {

    private final int id;
    private final Timestamp created;
    private final Employee employeeCreator;
    private final Location creatorLocation;
    private final String creatorUserAgent;
    private final InetAddress creatorIP;
    private final DataSource dataSource;

    @Override
    protected Connection openConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public TicketImpl(int id, DataSource dataSource) throws SQLException, NoSuchTicketException {
        this.id = id;
        this.dataSource = dataSource;
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT created, employee_creator, has_creator_location, creator_latitude, creator_longitude, creator_user_agent, creator_IP FROM ticket WHERE id = ?");
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

                        boolean hasCreatorLocation = resultSet.getBoolean("has_creator_location");
                        if (hasCreatorLocation) {
                            double latitude = resultSet.getDouble("creator_latitude");
                            double longitude = resultSet.getDouble("creator_longitude");
                            creatorLocation = new Location(latitude, longitude);
                        } else {
                            creatorLocation = null;
                        }

                        creatorUserAgent = resultSet.getString("creator_user_agent");

                        String creatorIPAddressString = resultSet.getString("creator_IP");
                        if (creatorIPAddressString == null) {
                            creatorIP = null;
                        } else {
                            try {
                                creatorIP = InetAddress.getByName(creatorIPAddressString);
                            } catch (UnknownHostException uhe) {
                                // This shouldn't happen as the address should be in the form of a numerical IP address.
                                throw new RuntimeException(uhe);
                            }
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

    public Location getCreatorLocation() {
        return creatorLocation;
    }

    public String getCreatorUserAgent() {
        return creatorUserAgent;
    }

    public InetAddress getCreatorIPAddress() {
        return creatorIP;
    }

}
