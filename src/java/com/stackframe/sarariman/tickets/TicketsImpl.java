/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import com.google.common.base.Function;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
                            result.add(new TicketImpl(resultSet.getInt("id"), dataSource));
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

    public Map<? extends Number, Ticket> getMap() {
        System.err.println("in getMap");
        Set<? extends Number> longKeys = ContiguousSet.create(Range.greaterThan(0L), DiscreteDomain.longs());
        Set<? extends Number> intKeys = ContiguousSet.create(Range.greaterThan(0), DiscreteDomain.integers());
        Set<? extends Number> keys = Sets.union(longKeys, intKeys);
        Function<Number, Ticket> f = new Function<Number, Ticket>() {
            public Ticket apply(Number n) {
                System.err.println("in apply, n=" + n);
                try {
                    return new TicketImpl(n.intValue(), dataSource);
                } catch (SQLException se) {
                    throw new RuntimeException(se);
                } catch (NoSuchTicketException nste) {
                    System.err.println("got exception: " + nste);
                    return null;
                }
            }

        };
        return Maps.asMap(keys, f);
    }

}
