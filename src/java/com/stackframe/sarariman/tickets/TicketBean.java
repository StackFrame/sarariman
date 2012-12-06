/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

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

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreated() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT created FROM ticket WHERE id = ?");
            try {
                query.setInt(1, getId());
                ResultSet resultSet = query.executeQuery();
                try {
                    if (resultSet.first()) {
                        return resultSet.getTimestamp("created");
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

}
