/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 *
 * @author mcculley
 */
public class SMSEvent {

    private final PhoneNumber from;

    private final PhoneNumber to;

    private final String body;

    private final long timestamp;

    private final String status;

    public SMSEvent(PhoneNumber from, PhoneNumber to, String body, long timestamp, String status) {
        this.from = from;
        this.to = to;
        this.body = body;
        this.timestamp = timestamp;
        this.status = status;
    }

    public PhoneNumber getFrom() {
        return from;
    }

    public PhoneNumber getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SMSEvent{" + "from=" + from + ", to=" + to + ", body='" + body + "', timestamp=" + timestamp + ", status=" + status + '}';
    }

}
