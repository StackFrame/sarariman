/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author mcculley
 */
public class EmailDispatcher {

    private final Executor executor = Executors.newFixedThreadPool(1);
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final InternetAddress from;
    private final Session session;

    public EmailDispatcher(String server, int port, String from) {
        try {
            this.from = new InternetAddress(from, true);
        } catch (AddressException ae) {
            throw new AssertionError(ae);
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", server);
        props.put("mail.smtp.port", Integer.toString(port));
        session = Session.getDefaultInstance(props, null);
    }

    private void submit(final Collection<InternetAddress> to, final Collection<InternetAddress> cc, final String subject, final String body) {
        executor.execute(new Runnable() {

            public void run() {
                try {
                    Message msg = new MimeMessage(session);
                    msg.setFrom(from);
                    Set<String> toAddresses = new HashSet<String>();
                    for (InternetAddress dest : to) {
                        msg.addRecipient(Message.RecipientType.TO, dest);
                        toAddresses.add(dest.getAddress());
                    }

                    if (cc != null) {
                        for (InternetAddress dest : cc) {
                            if (!toAddresses.contains(dest.getAddress())) {
                                msg.addRecipient(Message.RecipientType.CC, dest);
                            }
                        }
                    }

                    msg.setSubject(subject);
                    msg.setText(body);
                    msg.setHeader("X-Mailer", "Sarariman");
                    msg.setSentDate(new Date());
                    Transport.send(msg);
                } catch (MessagingException me) {
                    logger.log(Level.SEVERE, "caught exception trying to send email", me);
                }
            }

        });
    }

    public void send(Collection<InternetAddress> to, Collection<InternetAddress> cc, String subject, String body) {
        submit(to, cc, subject, body);
    }

    public void send(InternetAddress to, Collection<InternetAddress> cc, String subject, String body) {
        submit(Collections.singleton(to), cc, subject, body);
    }

    public static Collection<InternetAddress> addresses(Collection<Employee> employees) {
        List<InternetAddress> addresses = new ArrayList<InternetAddress>();
        for (Employee employee : employees) {
            addresses.add(employee.getEmail());
        }

        return addresses;
    }

}
