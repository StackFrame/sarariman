/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp;

import com.google.common.util.concurrent.Service;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface XMPPServer extends Service {

    Presence getPresence(String username);

    void setPresence(String username, Presence presence);

    Collection<Room> getRooms();

}
