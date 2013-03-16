/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.contacts;

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
class ContactImpl extends AbstractLinkable implements Contact {

    private final int id;
    private final DataSource dataSource;
    private final String servletPath;

    public ContactImpl(int id, DataSource dataSource, String servletPath) {
        this.id = id;
        this.dataSource = dataSource;
        this.servletPath = servletPath;
    }

    public int getId() {
        return id;
    }

    public String getName() throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT name FROM contacts WHERE id=?");
            try {
                query.setInt(1, id);
                ResultSet resultSet = query.executeQuery();
                try {
                    if (resultSet.next()) {
                        return resultSet.getString("name");
                    } else {
                        throw new AssertionError(String.format("no contact for id=%d", id));
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

    public URI getURI() {
        return URI.create(String.format("%s?id=%d", servletPath, id));
    }

}
