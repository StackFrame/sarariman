/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp.vysper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.vysper.xml.fragment.XMLSemanticError;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.DiscussionHistory;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Occupant;
import org.apache.vysper.xmpp.stanza.MessageStanza;

/**
 * This is just a subclass that overrides appending new elements to the history so that we can archive them. At some point we might
 * find a better way to archive room discussions and get rid of this.
 *
 * @author mcculley
 */
public class ArchivedDiscussionHistory extends DiscussionHistory {

    private final DataSource dataSource;

    private final Executor databaseWriteExecutor;

    private final Logger logger = Logger.getLogger(getClass());

    public ArchivedDiscussionHistory(DataSource dataSource, Executor databaseWriteExecutor) {
        this.dataSource = dataSource;
        this.databaseWriteExecutor = databaseWriteExecutor;
    }

    private void writeToDatabase(String from, String room, String message, long timestamp) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement s = c.prepareStatement(
                     "INSERT INTO conference_log (`from`, room, message, `timestamp`) " +
                     "VALUES(?, ?, ?, ?)");) {
            s.setString(1, from);
            s.setString(2, room);
            s.setString(3, message);
            s.setTimestamp(4, new Timestamp(timestamp));
            int numRowsInserted = s.executeUpdate();
            assert numRowsInserted == 1;
        } catch (SQLException e) {
            logger.error("exception when inserting message into conference_log", e);
        }
    }

    private void archive(final String from, final String room, final String message) {
        databaseWriteExecutor.execute(() -> {
            writeToDatabase(from, room, message, System.currentTimeMillis());
        });
    }

    @Override
    public void append(MessageStanza stanza, Occupant sender, Calendar timestamp) {
        Entity from = stanza.getFrom();
        Entity to = stanza.getTo();
        try {
            String body = stanza.getBody(null);
            archive(from.getBareJID().toString(), to.toString(), body);
        } catch (XMLSemanticError e) {
            logger.error("error retrieving body from message", e);
        }

        super.append(stanza, sender, timestamp);
    }

}
