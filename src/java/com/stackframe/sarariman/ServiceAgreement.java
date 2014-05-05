/*
 * Copyright (C) 2011-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joda.time.DateMidnight;

/**
 *
 * @author mcculley
 */
public class ServiceAgreement {

    private final int id;

    private final int project;

    private final DateMidnight popStart, popEnd;

    private final String billingPeriod;

    private final BigDecimal periodRate;

    private final String description;

    public ServiceAgreement(int id, int project, DateMidnight popStart, DateMidnight popEnd, String billingPeriod, BigDecimal periodRate, String description) {
        this.id = id;
        this.project = project;
        this.popStart = popStart;
        this.popEnd = popEnd;
        this.billingPeriod = billingPeriod;
        this.periodRate = periodRate;
        this.description = description;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public String getDescription() {
        return description;
    }

    public int getID() {
        return id;
    }

    public BigDecimal getPeriodRate() {
        return periodRate;
    }

    public DateMidnight getPopEnd() {
        return popEnd;
    }

    public DateMidnight getPopStart() {
        return popStart;
    }

    public int getProject() {
        return project;
    }

    @Override
    public String toString() {
        return String.format("{id=%d,project=%d,popStart=%s,popEnd=%s,billingPeriod=%s,periodRate=%s,description='%s'}",
                             id, project, popStart, popEnd, billingPeriod, periodRate, description);
    }

    public static ServiceAgreement lookup(Sarariman sarariman, int id) throws SQLException {
        try (Connection connection = sarariman.openConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * from service_agreements WHERE id=?");) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery();) {
                if (resultSet.next()) {
                    int project = resultSet.getInt("project");
                    DateMidnight popStart = new DateMidnight(resultSet.getDate("pop_start"));
                    DateMidnight popEnd = new DateMidnight(resultSet.getDate("pop_end"));
                    String billingPeriod = resultSet.getString("billing_period");
                    BigDecimal periodRate = new BigDecimal(resultSet.getString("period_rate"));
                    String description = resultSet.getString("description");
                    return new ServiceAgreement(id, project, popStart, popEnd, billingPeriod, periodRate, description);
                } else {
                    return null;
                }
            }
        }
    }

}
