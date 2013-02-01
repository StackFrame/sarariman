/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.contacts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ContactsImpl implements Contacts {

    protected static Connection openConnection() throws SQLException {
        try {
            DataSource source = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
            return source.getConnection();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Contact> getAll() throws SQLException {
        Connection connection = openConnection();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT id FROM contacts");
            try {
                ResultSet resultSet = query.executeQuery();
                try {
                    Collection<Contact> result = new ArrayList<Contact>();
                    while (resultSet.next()) {
                        result.add(new ContactImpl(resultSet.getInt("id")));
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
    }

}
