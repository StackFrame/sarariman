/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.conference;

import com.stackframe.sarariman.xmpp.Room;

/**
 *
 * @author mcculley
 */
public class ConferenceImpl implements Conference {

    private final String name;

    private final Room chatRoom;

    public ConferenceImpl(String name, Room chatRoom) {
        this.name = name;
        this.chatRoom = chatRoom;
    }

    public String getName() {
        return name;
    }

    public Room getChatRoom() {
        return chatRoom;
    }

}
