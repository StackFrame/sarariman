/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp.vysper;

import java.util.Calendar;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.DiscussionHistory;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Occupant;
import org.apache.vysper.xmpp.stanza.Stanza;

/**
 * This is just a subclass that overrides appending new elements to the history so that we can archive them. At some point we might
 * find a better way to archive room discussions and get rid of this.
 *
 * @author mcculley
 */
public class ArchivedDiscussionHistory extends DiscussionHistory {

    @Override
    public void append(Stanza stanza, Occupant sender, Calendar timestamp) {
        // FIXME archive to database
        System.err.println("stanza that would be archived sent to room . stanza=" + stanza);
        super.append(stanza, sender, timestamp);
    }

}