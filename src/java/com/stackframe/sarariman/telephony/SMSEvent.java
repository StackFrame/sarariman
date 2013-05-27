/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony;

/**
 *
 * @author mcculley
 */
public class SMSEvent {

    private final String from;

    private final String to;

    private final String body;

    private final long timestamp;

    public SMSEvent(String from, String to, String body, long timestamp) {
        this.from = from;
        this.to = to;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "SMSEvent{" + "from=" + from + ", to=" + to + ", body=" + body + ", timestamp=" + timestamp + '}';
    }

}
