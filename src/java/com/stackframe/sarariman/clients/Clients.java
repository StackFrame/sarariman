/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.clients;

import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Clients {

    Client get(int id);

    Iterable<Client> getAll();

    Map<? extends Number, Client> getMap();

    Client create(String name);

}
