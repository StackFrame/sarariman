/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.tickets;

/**
 *
 * @author mcculley
 */
public class NoSuchTicketException extends Exception {

    public NoSuchTicketException(int id) {
        super("ticket " + id + " does not exist");
    }

}
