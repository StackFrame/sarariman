/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Tickets {

    Ticket get(int id);

    Collection<String> getStatusTypes();

    Collection<Ticket> getAll();

    Map<? extends Number, Ticket> getMap();

}
