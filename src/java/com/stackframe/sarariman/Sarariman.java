package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class Sarariman {

    private final Connection connection;
    private final Directory directory;
    private final EmailDispatcher emailDispatcher;
    private final List<Employee> approvers = new ArrayList<Employee>();
    private final List<Employee> invoiceManagers = new ArrayList<Employee>();
    private final Timer timer = new Timer();

    public Sarariman(Directory directory, EmailDispatcher emailDispatcher) {
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;

        // FIXME: This should come from configuration
        approvers.add(directory.getByUserName().get("mcculley"));
        invoiceManagers.add(directory.getByUserName().get("mcculley"));
        invoiceManagers.add(directory.getByUserName().get("awetteland"));

        try {
            DataSource source = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
            connection = source.getConnection();
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
        return connection;
    }

    public Timer getTimer() {
        return timer;
    }

    public Map<Integer, Customer> getCustomers() throws SQLException {
        return Customer.getCustomers(this);
    }

    public Map<Integer, Project> getProjects() throws SQLException {
        return Project.getProjects(this);
    }

    @Override
    protected void finalize() throws Exception {
        connection.close();
    }

}
