/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.clients;

import com.stackframe.sarariman.AbstractLinkable;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ClientImpl extends AbstractLinkable implements Client {

    private final int id;
    private final DataSource dataSource;
    private final String servletPath;

    ClientImpl(int id, DataSource dataSource, String servletPath) {
        this.id = id;
        this.dataSource = dataSource;
        this.servletPath = servletPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT name FROM customers WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("name");
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

    public void setName(String name) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE customers SET name = ? WHERE id = ?");
                try {
                    s.setString(1, name);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public URI getURI() {
        return URI.create(String.format("%s?id=%d", servletPath, id));
    }

    public void delete() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM customers WHERE id=?");
                try {
                    ps.setLong(1, id);
                    ps.executeUpdate();
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
