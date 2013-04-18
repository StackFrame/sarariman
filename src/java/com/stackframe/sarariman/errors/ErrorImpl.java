/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.errors;

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
public class ErrorImpl extends AbstractLinkable implements Error {

    private final int id;
    private final DataSource dataSource;
    private final String mountPoint;

    ErrorImpl(int id, DataSource dataSource,String mountPoint) {
        this.id = id;
        this.dataSource = dataSource;this.mountPoint=mountPoint;
    }

    public int getId() {
        return id;
    }

    public String getStackTrace() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT exception FROM error_log WHERE id=?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        r.first();
                        return r.getString("exception");
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public URI getURI() {
        return URI.create(String.format("%serrors/view.jsp?id=%d", mountPoint, id));
    }

}
