/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp;

/**
 *
 * @author mcculley
 */
public class Presence {

    private final PresenceType type;

    private final String status;

    private final ShowType show;

    public Presence(PresenceType type, ShowType show, String status) {
        this.type = type;
        this.show = show;
        this.status = status;
    }

    public PresenceType getType() {
        return type;
    }

    public ShowType getShow() {
        return show;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(type);
        if (status != null) {
            buf.append(": " + status);
        }

        return buf.toString();
    }

}
