/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp.vysper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.MUCFeatures;
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

    private final Map<Entity, Room> rooms = new ConcurrentHashMap<>();

    private final DataSource dataSource;

    private final Executor databaseWriteExecutor;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public ArchivedRoomStorageProvider(DataSource dataSource, Executor databaseWriteExecutor) {
        this.dataSource = dataSource;
        this.databaseWriteExecutor = databaseWriteExecutor;
    }

    @Override
    public void initialize() {
    }

    @Override
    public Room createRoom(MUCFeatures features, Entity jid, String name, RoomType... roomTypes) {
        Room room = new ArchivedRoom(dataSource, databaseWriteExecutor, jid, name, roomTypes);
        rooms.put(jid, room);
        return room;
    }

    @Override
    public Collection<Room> getAllRooms() {
        return Collections.unmodifiableCollection(rooms.values());
    }

    @Override
    public Room findRoom(Entity jid) {
        return rooms.get(jid);
    }

    @Override
    public boolean roomExists(Entity jid) {
        logger.info("ArchivedRoomStorageProvider::roomExists entered. jid=" + jid);
        return rooms.containsKey(jid);
    }

    @Override
    public void deleteRoom(Entity jid) {
        logger.info("ArchivedRoomStorageProvider::deleteRoom entered. jid=" + jid);
        rooms.remove(jid);
    }

}
