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
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ContactsImpl implements Contacts {

    private final DataSource dataSource;
    private final String mountPoint;

    public ContactsImpl(DataSource dataSource, String mountPoint) {
        this.dataSource = dataSource;
        this.mountPoint = mountPoint;
    }

    public Contact get(int id) {
        return new ContactImpl(id, dataSource, mountPoint + "contact");
    }

    public Collection<Contact> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement query = connection.prepareStatement("SELECT id FROM contacts");
                try {
                    ResultSet resultSet = query.executeQuery();
                    try {
                        Collection<Contact> result = new ArrayList<Contact>();
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

}
