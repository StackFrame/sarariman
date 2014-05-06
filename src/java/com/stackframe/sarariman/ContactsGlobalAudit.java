/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.stackframe.sarariman.contacts.Contact;
import com.stackframe.sarariman.contacts.Contacts;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ContactsGlobalAudit implements Audit {

    private final DataSource dataSource;

    private final Contacts contacts;

    public ContactsGlobalAudit(DataSource dataSource, Contacts contacts) {
        this.dataSource = dataSource;
        this.contacts = contacts;
    }

    @Override
    public String getDisplayName() {
        return "Contacts";
    }

    private Set<Contact> projectTimesheetContacts() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT contact FROM project_timesheet_contacts");
             ResultSet rs = ps.executeQuery();) {
            ImmutableSet.Builder<Contact> setBuilder = ImmutableSet.<Contact>builder();
            while (rs.next()) {
                setBuilder.add(contacts.get(rs.getInt("contact")));
            }

            return setBuilder.build();
        }
    }

    private Set<Contact> projectInvoiceContacts() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT contact FROM project_invoice_contacts");
             ResultSet rs = ps.executeQuery();) {
            ImmutableSet.Builder<Contact> setBuilder = ImmutableSet.<Contact>builder();
            while (rs.next()) {
                setBuilder.add(contacts.get(rs.getInt("contact")));
            }

            return setBuilder.build();
        }
    }

    private Collection<Contact> orphanedContacts() throws SQLException {
        Set<Contact> orphaned = Sets.difference(Sets.difference(contacts.getAll(),
                                                                projectTimesheetContacts()),
                                                projectInvoiceContacts());
        return orphaned;
    }

    @Override
    public Collection<AuditResult> getResults() {
        try {
            Collection<Contact> orphanedContacts = orphanedContacts();
            ImmutableList.Builder<AuditResult> listBuilder = ImmutableList.<AuditResult>builder();
            for (Contact contact : orphanedContacts) {
                listBuilder.add(new AuditResult(AuditResultType.warning, String.format("%s is an orphaned contact",
                                                                                       contact.getName()), contact.getURL()));
            }

            return listBuilder.build();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
