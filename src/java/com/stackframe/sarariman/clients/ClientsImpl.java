/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.clients;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
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

}
