/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp;

import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface Room {

    String getName();

    Collection<Occupant> getOccupants();

    Collection<Message> getDiscussionHistory();

}
