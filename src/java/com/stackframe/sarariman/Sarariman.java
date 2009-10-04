package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.SQLException;
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
    private final Employee approver;
    private final Timer timer = new Timer();

    public Sarariman(Directory directory, EmailDispatcher emailDispatcher) {
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;

        // FIXME: This should come from configuration
        approver = directory.getByNumber().get(1);
        try {
            DataSource source = (DataSource)new InitialContext().lookup("jdbc/sarariman");
            connection = source.getConnection();
            // use connection
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

    public Employee getApprover() {
        return approver;
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

    @Override
    protected void finalize() throws Exception {
        connection.close();
    }

}
