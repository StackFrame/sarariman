/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Date;

/**
 *
 * @author mcculley
 */
public class EmailLogEntry {

    private final Employee sender;

    private final Date sent;

    public EmailLogEntry(Employee sender, Date sent) {
        this.sender = sender;
        this.sent = sent;
    }

    public Employee getSender() {
        return sender;
    }

    public Date getSent() {
        return sent;
    }

}
