package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class Sarariman {

    private final Directory directory;
    private final EmailDispatcher emailDispatcher;

    public Sarariman(Directory directory, EmailDispatcher emailDispatcher) {
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;
    }

    public Directory getDirectory() {
        return directory;
    }

    public EmailDispatcher getEmailDispatcher() {
        return emailDispatcher;
    }

}
