/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp;

/**
 *
 * @author mcculley
 */
public class Message {

    private final Entity from, to;

    private final String message;

    private final long timestamp;

    public Message(Entity from, Entity to, String message, long timestamp) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Entity getFrom() {
        return from;
    }

    public Entity getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
