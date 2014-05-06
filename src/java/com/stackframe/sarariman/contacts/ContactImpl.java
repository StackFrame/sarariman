/*
 * Copyright (C) 2013-2014 StackFrame, LLC
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement query = connection.prepareStatement("SELECT name FROM contacts WHERE id=?");) {
            query.setInt(1, id);
            try (ResultSet resultSet = query.executeQuery();) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                } else {
                    throw new AssertionError(String.format("no contact for id=%d", id));
                }
            }
        }
    }

    @Override
    public URI getURI() {
        return URI.create(String.format("%s?id=%d", servletPath, id));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContactImpl other = (ContactImpl)obj;
        return this.id == other.id;
    }

}
