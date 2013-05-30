/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp.vysper;

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

    private final DiscussionHistory archivedDiscussionHistory = new ArchivedDiscussionHistory();

    public ArchivedRoom(Entity jid, String name, RoomType... types) {
        super(jid, name, types);
    }

    @Override
    public DiscussionHistory getHistory() {
        return archivedDiscussionHistory;
    }

}
