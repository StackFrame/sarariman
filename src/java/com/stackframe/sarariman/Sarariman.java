package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class Sarariman {

    private final Directory directory;
    private final EmailDispatcher emailDispatcher;
    private final Employee approver;

    public Sarariman(Directory directory, EmailDispatcher emailDispatcher) {
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;

        // FIXME: This should come from configuration
        approver = directory.getEmployeeMap().get(1);
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

}
