/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp;

/**
 *
 * @author mcculley
 */
public class Occupant {

    private final Entity entity;

    public Occupant(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}
