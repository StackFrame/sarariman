/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.stackframe.sarariman.contacts.Contact;
import com.stackframe.sarariman.contacts.Contacts;
import com.stackframe.sarariman.contacts.ContactsImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mcculley
 */
public class ContactsGlobalAudit implements Audit {

    private final Sarariman sarariman;

    public ContactsGlobalAudit(Sarariman sarariman) {
        this.sarariman = sarariman;
    }

    public String getDisplayName() {
        return "Contacts";
    }

    private Set<Integer> projectTimesheetContacts() throws SQLException {
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT contact FROM project_timesheet_contacts");
            try {
                ResultSet rs = ps.executeQuery();
                ImmutableSet.Builder<Integer> setBuilder = ImmutableSet.<Integer>builder();
                while (rs.next()) {
                    setBuilder.add(rs.getInt("contact"));
                }

                return setBuilder.build();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    private Set<Integer> projectInvoiceContacts() throws SQLException {
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT contact FROM project_invoice_contacts");
            try {
                ResultSet rs = ps.executeQuery();
                ImmutableSet.Builder<Integer> setBuilder = ImmutableSet.<Integer>builder();
                while (rs.next()) {
                    setBuilder.add(rs.getInt("contact"));
                }

                return setBuilder.build();
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    private Collection<Contact> orphanedContacts() throws SQLException {
        Contacts contacts = new ContactsImpl();
        Map<Integer, Contact> map = new HashMap<Integer, Contact>();
        for (Contact contact : contacts.getAll()) {
            map.put(contact.getId(), contact);
        }
        
        Set<Integer> keys = map.keySet();
        keys.removeAll(projectTimesheetContacts());
        keys.removeAll(projectInvoiceContacts());
        return map.values();
    }

    public Collection<AuditResult> getResults() {
        try {
            Collection<Contact> orphanedContacts = orphanedContacts();
            ImmutableList.Builder<AuditResult> listBuilder = ImmutableList.<AuditResult>builder();
            for (Contact contact : orphanedContacts) {
                listBuilder.add(new AuditResult(AuditResultType.warning, String.format("%s is an orphaned contact", contact.getName())));
            }

            return listBuilder.build();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
