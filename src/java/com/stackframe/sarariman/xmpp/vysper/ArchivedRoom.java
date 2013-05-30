/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp.vysper;

import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.DiscussionHistory;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Room;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.RoomType;

/**
 * This is just a subclass of Room so that we can archive the history of room discussions. At some point we might find a better way
 * to archive room discussions and get rid of this.
 *
 * @author mcculley
 */
public class ArchivedRoom extends Room {

    private final DiscussionHistory archivedDiscussionHistory;

    public ArchivedRoom(DataSource dataSource, Executor databaseWriteExecutor, Entity jid, String name, RoomType... types) {
        super(jid, name, types);
        archivedDiscussionHistory = new ArchivedDiscussionHistory(dataSource, databaseWriteExecutor);
    }

    @Override
    public DiscussionHistory getHistory() {
        return archivedDiscussionHistory;
    }

}
