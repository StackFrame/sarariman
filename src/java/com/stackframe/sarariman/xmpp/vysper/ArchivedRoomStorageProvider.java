/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp.vysper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Room;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.RoomType;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.storage.RoomStorageProvider;

/**
 * This is just an implementation of RoomStorageProvider that is the same as InMemoryRoomStorageProvider but lets us return an
 * ArchivedRoom so that we can archive room discussions. At some point we might find a better way to archive room discussions and
 * get rid of this.
 *
 * @author mcculley
 */
public class ArchivedRoomStorageProvider implements RoomStorageProvider {

    private Map<Entity, Room> rooms = new ConcurrentHashMap<Entity, Room>();

    public void initialize() {
        // do nothing
    }

    public Room createRoom(Entity jid, String name, RoomType... roomTypes) {
        Room room = new ArchivedRoom(jid, name, roomTypes);
        rooms.put(jid, room);
        return room;
    }

    public Collection<Room> getAllRooms() {
        return Collections.unmodifiableCollection(rooms.values());
    }

    public Room findRoom(Entity jid) {
        return rooms.get(jid);
    }

    public boolean roomExists(Entity jid) {
        return rooms.containsKey(jid);
    }

    public void deleteRoom(Entity jid) {
        rooms.remove(jid);
    }

}
