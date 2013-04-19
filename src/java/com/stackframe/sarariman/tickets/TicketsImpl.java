/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.stackframe.base.Numbers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TicketsImpl implements Tickets {

    private final DataSource dataSource;
    private final String mountPoint;

    public TicketsImpl(DataSource dataSource, String mountPoint) {
        this.dataSource = dataSource;
        this.mountPoint = mountPoint;
    }

    public Ticket get(int id) {
        try {
            return new TicketImpl(id, dataSource, mountPoint + "tickets");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<String> getStatusTypes() {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement query = connection.prepareStatement("SELECT name FROM ticket_status_type");
            try {
                ResultSet resultSet = query.executeQuery();
                try {
                    Collection<String> result = new ArrayList<String>();
                    try {
                        while (resultSet.next()) {
                            result.add(resultSet.getString("name"));
                        }

                        return result;
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

    public Collection<Ticket> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement query = connection.prepareStatement("SELECT id FROM ticket");
                try {
                    ResultSet resultSet = query.executeQuery();
                    try {
                        Collection<Ticket> result = new ArrayList<Ticket>();
                        while (resultSet.next()) {
                            result.add(get(resultSet.getInt("id")));
                        }

                        return result;
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

    public Map<? extends Number, Ticket> getMap() {
        Function<Number, Ticket> f = new Function<Number, Ticket>() {
            public Ticket apply(Number n) {
                return get(n.intValue());
            }

        };
        return Maps.asMap(Numbers.positiveIntegers, f);
    }

}
