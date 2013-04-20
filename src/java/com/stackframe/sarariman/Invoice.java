/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Preconditions;
import com.stackframe.sarariman.clients.Client;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.tasks.Task;
import com.stackframe.sarariman.tasks.Tasks;
import static com.stackframe.sql.SQLUtilities.convert;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import javax.sql.DataSource;

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

    public static Invoice create(Sarariman sarariman, Client client, Project project, String popStart, String popEnd, Map parameterMap, String[] employees, String[] tasks, String[] dates, String[] billedServices) throws SQLException, ParseException {
        Preconditions.checkNotNull(client);
        if (employees == null) {
            employees = new String[0];
        }

        if (tasks == null) {
            tasks = new String[0];
        }

        if (dates == null) {
            dates = new String[0];
        }

        int numItems = employees.length;
        if (numItems != tasks.length || numItems != dates.length) {
            System.err.println("mismatched lengths");
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Connection connection = sarariman.openConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement createInvoice = connection.prepareStatement("INSERT INTO invoice_info (sent, customer, project, pop_start, pop_end, description) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            createInvoice.setDate(1, convert(new java.util.Date()));
            createInvoice.setLong(2, client.getId());
            createInvoice.setLong(3, project.getId());
            createInvoice.setDate(4, convert(dateFormat.parse(popStart)));
            createInvoice.setDate(5, convert(dateFormat.parse(popEnd)));
            createInvoice.setString(6, "invoice for " + client.getName() + " - " + project.getName());
            int createdRowCount = createInvoice.executeUpdate();
            assert createdRowCount == 1;
            ResultSet keys = createInvoice.getGeneratedKeys();
            keys.next();
            int key = keys.getInt(1);
            String id = Integer.toString(key);
            createInvoice.close();

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

            if (billedServices != null) {
                for (String billedService : billedServices) {
                    PreparedStatement ps = connection.prepareStatement("UPDATE billed_services SET invoice=? WHERE id=?");
                    try {
                        ps.setString(1, id);
                        ps.setString(2, billedService);
                        int rowCount = ps.executeUpdate();
                        if (rowCount != 1) {
                            System.err.println("could not invoice billed service for id=" + billedService);
                        }
                    } finally {
                        ps.close();
                    }
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
// FIXME: Add hyperlink to invoice.
            sarariman.getEmailDispatcher().send(EmailDispatcher.addresses(sarariman.getInvoiceManagers()), null, "invoice created",
                                                "Invoice " + id + " was created.");
            return new Invoice(id, sarariman);
        } finally {
            connection.close();
        }
    }

    public void delete() throws SQLException {
        Connection connection = sarariman.openConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement deleteHours = connection.prepareStatement("DELETE FROM invoices WHERE id = ?");
            try {
                deleteHours.setString(1, id);
                int rowCount = deleteHours.executeUpdate();
                if (rowCount == 0) {
                    System.err.println("could not delete invoice " + id);
                } else {
                    PreparedStatement deleteInfo = connection.prepareStatement("DELETE FROM invoice_info WHERE id = ?");
                    try {
                        deleteInfo.setString(1, id);
                        rowCount = deleteInfo.executeUpdate();
                        if (rowCount != 1) {
                            System.err.println("could not delete invoice " + id);
                        } else {
                            PreparedStatement deleteExpenses = connection.prepareStatement("UPDATE expenses SET invoice = NULL WHERE invoice = ?");
                            try {
                                deleteExpenses.setString(1, id);
                                deleteExpenses.executeUpdate();
                            } finally {
                                deleteExpenses.close();
                            }
                        }
                    } finally {
                        deleteInfo.close();
                    }
                }
            } finally {
                deleteHours.close();
            }

            connection.commit();
            connection.setAutoCommit(true);

            sarariman.getEmailDispatcher().send(EmailDispatcher.addresses(sarariman.getInvoiceManagers()), null, "invoice deleted",
                                                "Invoice " + id + " was deleted.");
        } finally {
            connection.close();
        }
    }

    public static CostData cost(Sarariman sarariman, int project, int employee, int task, Date date, double duration) throws SQLException {
        Map<Long, LaborCategory> categoriesById = sarariman.getLaborCategories();
        Collection<LaborCategoryAssignment> projectBillRates = sarariman.getProjectBillRates();
        return cost(sarariman, categoriesById, projectBillRates, project, employee, task, date, duration);
    }

    public static CostData cost(Sarariman sarariman, Map<Long, LaborCategory> categoriesById, Collection<LaborCategoryAssignment> projectBillRates, int project, int employee, int task_id, Date date, double duration) throws SQLException {
        return cost(categoriesById, projectBillRates, project, employee, task_id, date, duration, sarariman.getTasks(), sarariman.getDataSource());
    }

    public static CostData cost(Map<Long, LaborCategory> categoriesById, Collection<LaborCategoryAssignment> projectBillRates, int project, int employee, int task_id, Date date, double duration, Tasks tasks, DataSource dataSource) throws SQLException {
        return cost(categoriesById, projectBillRates, project, employee, tasks.get(task_id), date, duration, dataSource);
    }

    public static CostData cost(Map<Long, LaborCategory> categoriesById, Collection<LaborCategoryAssignment> projectBillRates,
                                int project, int employee, Task task, Date date, double duration, DataSource dataSource)
            throws SQLException {
        Connection c = dataSource.getConnection();
        try {
            PreparedStatement s = c.prepareStatement(
                    "SELECT labor_categories.rate, labor_categories.id " +
                    "FROM labor_categories " +
                    "JOIN labor_category_assignments ON labor_categories.id = labor_category_assignments.labor_category " +
                    "WHERE labor_categories.project = ? AND " +
                    "labor_category_assignments.employee = ? AND " +
                    "labor_categories.pop_start <= ? AND " +
                    "labor_categories.pop_end >= ? AND " +
                    "labor_category_assignments.pop_start <= ? AND " +
                    "labor_category_assignments.pop_end >= ?");
            try {
                s.setInt(1, project);
                s.setInt(2, employee);
                s.setDate(3, date);
                s.setDate(4, date);
                s.setDate(5, date);
                s.setDate(6, date);
                ResultSet r = s.executeQuery();
                if (r.first()) {
                    BigDecimal rate;
                    BigDecimal cost;
                    Long categoryId = r.getLong("id");
                    if (task.isBillable()) {
                        rate = r.getBigDecimal("rate").setScale(2);
                        cost = rate.multiply(new BigDecimal(duration));
                        cost = cost.setScale(2, RoundingMode.UP);
                    } else {
                        rate = cost = BigDecimal.ZERO;
                    }

                    return new CostData(cost, categoriesById.get(categoryId), rate);
                } else {
                    return new CostData(new BigDecimal(0), null, new BigDecimal(0));
                }
            } finally {
                s.close();
            }
        } finally {
            c.close();
        }
    }

    public String getId() {
        return id;
    }

}
