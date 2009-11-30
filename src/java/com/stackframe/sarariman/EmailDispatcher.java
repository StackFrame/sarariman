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
    private final String host;
    private final int port;

    public EmailDispatcher(Properties properties) {
        try {
            this.from = new InternetAddress((String)properties.get("mail.from"), true);
        } catch (AddressException ae) {
            throw new AssertionError(ae);
        }

        session = Session.getDefaultInstance(properties, null);
        // FIXME: We shouldn't need to set these parameters individually instead of just letting the Session and Transport handle
        // the Properties, but if we don't, it doesn't seem to respect mail.smtp.port.
        host = (String)properties.get("mail.smtp.host");
        port = (Integer)properties.get("mail.smtp.port");
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
                    msg.saveChanges();
                    Transport transport = session.getTransport("smtp");
                    transport.connect(host, port, null, null);
                    transport.sendMessage(msg, msg.getAllRecipients());
                    transport.close();
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
