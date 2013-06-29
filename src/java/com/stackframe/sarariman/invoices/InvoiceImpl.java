/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.invoices;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class InvoiceImpl implements Invoice {

    private final int id;

    private final DataSource dataSource;

    private final Credits credits;

    InvoiceImpl(int id, DataSource dataSource, Credits credits) {
        this.id = id;
        this.dataSource = dataSource;
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getCreditsTotal() {
        BigDecimal v = BigDecimal.ZERO;
        for (Credit c : getCredits()) {
            v = v.add(c.getAmount());
        }

        return v;
    }

    public Collection<Credit> getCredits() {
        System.err.println("in getCredits");
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement("SELECT id FROM credit WHERE invoice=?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        Collection<Credit> result = new ArrayList<Credit>();
                        while (r.next()) {
                            result.add(credits.get(r.getInt("id")));
                        }

                        return result;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
