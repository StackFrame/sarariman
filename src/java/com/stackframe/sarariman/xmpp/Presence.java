/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */package com.stackframe.sarariman.xmpp;

/**
 *
 * @author mcculley
 */
public class Presence {

    private final PresenceType type;

    private final String status;

    public Presence(PresenceType type, String status) {
        this.type = type;
        this.status = status;
    }

    public PresenceType getType() {
        return type;
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
