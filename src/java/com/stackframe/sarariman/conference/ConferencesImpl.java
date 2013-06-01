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
        for (Room chatRoom : xmpp.getRooms()) {
            Conference conference = new ConferenceImpl(chatRoom.getName(), chatRoom);
            conferences.add(conference);
        }

        return conferences;
    }

    public Conference get(String name) {
        for (Room chatRoom : xmpp.getRooms()) {
            if (name.equals(chatRoom.getName())) {
                Conference conference = new ConferenceImpl(chatRoom.getName(), chatRoom);
                return conference;
            }
        }

        return null;
    }

}
