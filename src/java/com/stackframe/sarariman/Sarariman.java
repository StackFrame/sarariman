/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class Sarariman {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private Connection connection;
    private final Directory directory;
    private final EmailDispatcher emailDispatcher;
    private final List<Employee> approvers = new ArrayList<Employee>();
    private final List<Employee> invoiceManagers = new ArrayList<Employee>();
    private final Timer timer = new Timer();
    /** Do not edit this.  It is set by Subversion. */
    private final String revision = "$Revision$";

    private String getRevision() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < revision.length(); i++) {
            char c = revision.charAt(i);
            if (Character.isDigit(c)) {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    public String getVersion() {
        return "1.0.14r" + getRevision();
    }

    public Sarariman(LDAPDirectory directory, EmailDispatcher emailDispatcher) {
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;

        // FIXME: This should come from configuration
        approvers.add(directory.getByUserName().get("mcculley"));
        invoiceManagers.add(directory.getByUserName().get("mcculley"));
        invoiceManagers.add(directory.getByUserName().get("awetteland"));

        connection = openConnection();
        scheduleTasks(emailDispatcher, directory);
    }

    private void scheduleTasks(EmailDispatcher emailDispatcher, final LDAPDirectory directory) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        final long ONE_SECOND = 1000;
        final long ONE_MINUTE = 60 * ONE_SECOND;
        final long ONE_HOUR = 60 * ONE_MINUTE;
        final long ONE_DAY = 24 * ONE_HOUR;

        final TimerTask weeknightTask = new WeeknightTask(this, directory, emailDispatcher);
        timer.scheduleAtFixedRate(weeknightTask, date, ONE_DAY);

        final TimerTask reloadLDAP = new TimerTask() {

            public void run() {
                directory.reload();
            }

        };
        timer.schedule(reloadLDAP, ONE_HOUR, ONE_HOUR);
    }

    private Connection openConnection() {
        try {
            DataSource source = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
            return source.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public EmailDispatcher getEmailDispatcher() {
        return emailDispatcher;
    }

    public List<Employee> getApprovers() {
        return approvers;
    }

    public List<Employee> getInvoiceManagers() {
        return invoiceManagers;
    }

    public Connection getConnection() {
        try {
            if (connection.isClosed()) {
                connection = openConnection();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return connection;
    }

    public Timer getTimer() {
        return timer;
    }

    public Map<Long, Customer> getCustomers() throws SQLException {
        return Customer.getCustomers(this);
    }

    public Map<Long, Project> getProjects() throws SQLException {
        return Project.getProjects(this);
    }

    public Collection<Task> getTasks() throws SQLException {
        return Task.getTasks(this);
    }

    void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "exception while closing connection", e);
        }

        timer.cancel();
    }

    @Override
    protected void finalize() throws Exception {
        connection.close();
    }

}
