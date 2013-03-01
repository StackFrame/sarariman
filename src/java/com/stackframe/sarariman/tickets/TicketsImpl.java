/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TicketsImpl implements Tickets {

    private final DataSource dataSource;

    public TicketsImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Collection<String> getStatusTypes() throws SQLException {
        Collection<String> result = new ArrayList<String>();
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT name FROM ticket_status_type");
            try {
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        result.add(resultSet.getString("name"));
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

        return result;
    }

    public Collection<Ticket> getAll() throws SQLException {
        Collection<Ticket> result = new ArrayList<Ticket>();
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT id FROM ticket");
            try {
                ResultSet resultSet = query.executeQuery();
                try {
                    while (resultSet.next()) {
                        try {
                            result.add(new TicketImpl(resultSet.getInt("id")));
                        } catch (NoSuchTicketException nste) {
                            throw new AssertionError(nste);
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

        return result;
    }

}
