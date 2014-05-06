/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.contacts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
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

    @Override
    public Contact get(int id) {
        return new ContactImpl(id, dataSource, mountPoint + "contact");
    }

    @Override
    public Set<Contact> getAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement query = connection.prepareStatement("SELECT id FROM contacts");
             ResultSet resultSet = query.executeQuery();) {
            Set<Contact> result = new HashSet<>();
            while (resultSet.next()) {
                result.add(get(resultSet.getInt("id")));
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
