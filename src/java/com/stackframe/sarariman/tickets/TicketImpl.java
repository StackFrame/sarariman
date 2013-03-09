/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.stackframe.sarariman.Employee;
import java.net.InetAddress;
import java.net.URI;
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
    private final DataSource dataSource;
    private final String servletPath;

    @Override
    protected Connection openConnection() throws SQLException {
        return dataSource.getConnection();
    }

    TicketImpl(int id, DataSource dataSource, String servletPath) throws SQLException {
        this.id = id;
        this.dataSource = dataSource;
        this.servletPath = servletPath;
    }

    public int getId() {
        return id;
    }

    public Timestamp getCreated() {
        try {
            Connection connection = openConnection();
            try {
                PreparedStatement query = connection.prepareStatement("SELECT created FROM ticket WHERE id = ?");
                try {
                    query.setInt(1, id);
                    ResultSet resultSet = query.executeQuery();
                    try {
                        boolean hasRow = resultSet.first();
                        assert hasRow;
                        return resultSet.getTimestamp("created");
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    query.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee getEmployeeCreator() {
        try {
            Connection connection = openConnection();
            try {
                PreparedStatement query = connection.prepareStatement("SELECT employee_creator FROM ticket WHERE id = ?");
                try {
                    query.setInt(1, id);
                    ResultSet resultSet = query.executeQuery();
                    try {
                        boolean hasRow = resultSet.first();
                        assert hasRow;
                        int employeeCreatorID = resultSet.getInt("employee_creator");
                        if (resultSet.wasNull()) {
                            return null;
                        } else {
                            return getDirectory().getByNumber().get(employeeCreatorID);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getCreatorLocation() {
        try {
            Connection connection = openConnection();
            try {
                PreparedStatement query = connection.prepareStatement("SELECT has_creator_location, creator_latitude, creator_longitude FROM ticket WHERE id = ?");
                try {
                    query.setInt(1, id);
                    ResultSet resultSet = query.executeQuery();
                    try {
                        boolean hasRow = resultSet.first();
                        assert hasRow;
                        boolean hasCreatorLocation = resultSet.getBoolean("has_creator_location");
                        if (hasCreatorLocation) {
                            double latitude = resultSet.getDouble("creator_latitude");
                            double longitude = resultSet.getDouble("creator_longitude");
                            return new Location(latitude, longitude);
                        } else {
                            return null;
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCreatorUserAgent() {
        try {
            Connection connection = openConnection();
            try {
                PreparedStatement query = connection.prepareStatement("SELECT creator_user_agent FROM ticket WHERE id = ?");
                try {
                    query.setInt(1, id);
                    ResultSet resultSet = query.executeQuery();
                    try {
                        boolean hasRow = resultSet.first();
                        assert hasRow;
                        return resultSet.getString("creator_user_agent");
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    query.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public InetAddress getCreatorIPAddress() {
        try {
            Connection connection = openConnection();
            try {
                PreparedStatement query = connection.prepareStatement("SELECT creator_IP FROM ticket WHERE id = ?");
                try {
                    query.setInt(1, id);
                    ResultSet resultSet = query.executeQuery();
                    try {
                        boolean hasRow = resultSet.first();
                        assert hasRow;
                        String creatorIPAddressString = resultSet.getString("creator_IP");
                        if (creatorIPAddressString == null) {
                            return null;
                        } else {
                            try {
                                return InetAddress.getByName(creatorIPAddressString);
                            } catch (UnknownHostException uhe) {
                                // This shouldn't happen as the address should be in the form of a numerical IP address.
                                throw new RuntimeException(uhe);
                            }
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public URI getURI() {
        return URI.create(String.format("%s/%d", servletPath, id));
    }

}
