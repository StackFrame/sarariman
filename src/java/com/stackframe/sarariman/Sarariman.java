package com.stackframe.sarariman;

import java.sql.Connection;
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

    public Sarariman(Directory directory, EmailDispatcher emailDispatcher) {
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;

        // FIXME: This should come from configuration
        approver = directory.getEmployeeMap().get(1);
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

}
