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

/**
 *
 * @author mcculley
 */
class ContactImpl implements Contact {

    private int id;

    ContactImpl(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() throws SQLException {
        Connection connection = ContactsImpl.openConnection();
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

}
