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
public interface Conference {

    String getName();

    Room getChatRoom();

}
