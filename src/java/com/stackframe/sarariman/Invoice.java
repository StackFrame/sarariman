/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class Invoice {

    private final String id;
    private final Sarariman sarariman;

    public Invoice(String id, Sarariman sarariman) {
        this.id = id;
        this.sarariman = sarariman;
    }

    public static Invoice create(Sarariman sarariman, String id, Map parameterMap, String[] employees, String[] tasks, String[] dates) throws SQLException {
        Invoice invoice = new Invoice(id, sarariman);
        int numItems = employees.length;
        if (numItems != tasks.length || numItems != dates.length) {
            System.err.println("mismatched lengths");
        }

        Connection connection = sarariman.getConnection();
        connection.setAutoCommit(false);
        for (int i = 0; i < numItems; i++) {
            String toAdd = "addToInvoice" + i;
            if (parameterMap.containsKey(toAdd)) {
                int employeeNumber = Integer.parseInt(employees[i]);
                int task = Integer.parseInt(tasks[i]);
                Date date = Date.valueOf(dates[i]);
                PreparedStatement ps = connection.prepareStatement("INSERT INTO invoices (id, employee, task, date) VALUES(?, ?, ?, ?)");
                try {
                    ps.setString(1, id);
                    ps.setInt(2, employeeNumber);
                    ps.setInt(3, task);
                    ps.setDate(4, date);
                    int rowCount = ps.executeUpdate();
                    if (rowCount != 1) {
                        System.err.println("could not insert invoice row for date=" + date + " and employee=" + employeeNumber);
                    }
                } finally {
                    ps.close();
                }
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
        sarariman.getEmailDispatcher().send(EmailDispatcher.addresses(sarariman.getInvoiceManagers()), null, "invoice created",
                "Invoice " + id + " was created.");

        return invoice;
    }

}
