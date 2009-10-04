package com.stackframe.sarariman;

import java.util.TimerTask;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author mcculley
 */
public class WeeknightTask extends TimerTask {

    private final Directory directory;
    private final EmailDispatcher emailDispatcher;

    public WeeknightTask(Directory directory, EmailDispatcher emailDispatcher) {
        this.directory = directory;
        this.emailDispatcher = emailDispatcher;
    }

    public void run() {
        System.err.println("Running the nightly task");
        try {
            emailDispatcher.send(new InternetAddress("mcculley@stackframe.com", true), "Sarariman running nightly task",
                    "Sarariman is running the nightly task.");
        } catch (AddressException ae) {
            throw new RuntimeException(ae);
        }

        for (Employee employee : directory.getEmployees()) {
        }
    }

}
