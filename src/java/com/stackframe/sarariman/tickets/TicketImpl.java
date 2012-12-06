/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman.tickets;

/**
 *
 * @author mcculley
 */
public class TicketImpl extends AbstractTicket {

    private final int id;

    public TicketImpl(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
