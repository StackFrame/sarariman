/*
 * Copyright (C) 2011-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateMidnight;

/**
 *
 * @author mcculley
 */
public class BilledService implements Comparable<BilledService> {

    private final int id;
    private final int serviceAgreement;
    private final DateMidnight popStart, popEnd;
    private final String invoice;

    public BilledService(int id, int serviceAgreement, DateMidnight popStart, DateMidnight popEnd, String invoice) {
        this.id = id;
        this.serviceAgreement = serviceAgreement;
        this.popStart = popStart;
        this.popEnd = popEnd;
        this.invoice = invoice;
    }

    public int getID() {
        return id;
    }

    public String getInvoice() {
        return invoice;
    }

    public DateMidnight getPopEnd() {
        return popEnd;
    }

    public DateMidnight getPopStart() {
        return popStart;
    }

    public int getServiceAgreement() {
        return serviceAgreement;
    }

    public int compareTo(BilledService t) {
        return popStart.compareTo(t.popStart);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final BilledService other = (BilledService)obj;
        if (this.serviceAgreement != other.serviceAgreement) {
            return false;
        }

        if (this.popStart != other.popStart && (this.popStart == null || !this.popStart.equals(other.popStart))) {
            return false;
        }

        if (this.popEnd != other.popEnd && (this.popEnd == null || !this.popEnd.equals(other.popEnd))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.serviceAgreement;
        hash = 97 * hash + (this.popStart != null ? this.popStart.hashCode() : 0);
        hash = 97 * hash + (this.popEnd != null ? this.popEnd.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "{" + "id=" + id + ",serviceAgreement=" + serviceAgreement + ",popStart=" + popStart + ",popEnd=" + popEnd + ",invoice=" + invoice + '}';
    }

    public static BilledService lookup(Sarariman sarariman, int id) throws SQLException {
        Connection connection = sarariman.getDataSource().getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * from billed_services WHERE id=?");
            try {
                ps.setInt(1, id);
                ResultSet resultSet = ps.executeQuery();
                try {
                    if (resultSet.next()) {
                        int serviceAgreement = resultSet.getInt("service_agreement");
                        DateMidnight popStart = new DateMidnight(resultSet.getDate("pop_start"));
                        DateMidnight popEnd = new DateMidnight(resultSet.getDate("pop_end"));
                        String invoice = resultSet.getString("invoice");
                        return new BilledService(id, serviceAgreement, popStart, popEnd, invoice);
                    } else {
                        return null;
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

    public static List<BilledService> lookupByServiceAgreement(Sarariman sarariman, int serviceAgreement) throws SQLException {
        Connection connection = sarariman.getDataSource().getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * from billed_services WHERE service_agreement=?");
            try {
                ps.setInt(1, serviceAgreement);
                ResultSet resultSet = ps.executeQuery();
                try {
                    List<BilledService> billedServices = new ArrayList<BilledService>();
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        DateMidnight popStart = new DateMidnight(resultSet.getDate("pop_start"));
                        DateMidnight popEnd = new DateMidnight(resultSet.getDate("pop_end"));
                        String invoice = resultSet.getString("invoice");
                        billedServices.add(new BilledService(id, serviceAgreement, popStart, popEnd, invoice));
                    }

                    return billedServices;
                } finally {
                    resultSet.close();
                }
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }

}
