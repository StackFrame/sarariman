/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.clients;

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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ClientsImpl implements Clients {

    private final DataSource dataSource;

    public ClientsImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Iterable<Client> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery("SELECT id FROM customers");
                    try {
                        Collection<Client> c = new ArrayList<Client>();
                        while (r.next()) {
                            c.add(new ClientImpl(r.getInt("id"), dataSource));
                        }

                        return c;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Map<? extends Number, Client> getMap() {
        Set<? extends Number> longKeys = ContiguousSet.create(Range.greaterThan(0L), DiscreteDomain.longs());
        Set<? extends Number> intKeys = ContiguousSet.create(Range.greaterThan(0), DiscreteDomain.integers());
        Set<? extends Number> keys = Sets.union(longKeys, intKeys);
        Function<Number, Client> f = new Function<Number, Client>() {
            public Client apply(Number n) {
                return new ClientImpl(n.intValue(), dataSource);
            }

        };
        return Maps.asMap(keys, f);
    }

    public Client create(String name) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO customers (name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
                try {
                    ps.setString(1, name);
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    try {
                        rs.next();
                        return new ClientImpl(rs.getInt(1), dataSource);
                    } finally {
                        rs.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
