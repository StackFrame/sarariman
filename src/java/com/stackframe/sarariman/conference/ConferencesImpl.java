/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.conference;

import com.stackframe.sarariman.xmpp.Room;
import com.stackframe.sarariman.xmpp.XMPPServer;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public class ConferencesImpl implements Conferences {

    private final XMPPServer xmpp;

    public ConferencesImpl(XMPPServer xmpp) {
        this.xmpp = xmpp;
    }

    public Collection<Conference> getAll() {
        Collection<Conference> conferences = new ArrayList<Conference>();
        Collection<Room> chatRooms = xmpp.getRooms();
        for (Room chatRoom : chatRooms) {
            Conference conference = new ConferenceImpl(chatRoom.getName(), chatRoom);
            conferences.add(conference);
        }

        return conferences;
    }

}
