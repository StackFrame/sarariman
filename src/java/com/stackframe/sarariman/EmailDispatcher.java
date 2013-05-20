/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.stackframe.reflect.ReflectionUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author mcculley
 */
public class EmailDispatcher {

    private final Executor executor;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final InternetAddress defaultFrom;
    private final Session session;
    private final String host;
    private final int port;
    private final boolean inhibit;

    public EmailDispatcher(Properties properties, boolean inhibit, Executor executor) {
        this.inhibit = inhibit;
        this.executor = executor;
        try {
            this.defaultFrom = new InternetAddress((String)properties.get("mail.from"), true);
        } catch (AddressException ae) {
            throw new AssertionError(ae);
        }

        session = Session.getDefaultInstance(properties, null);
        // FIXME: We shouldn't need to set these parameters individually instead of just letting the Session and Transport handle
        // the Properties, but if we don't, it doesn't seem to respect mail.smtp.port.
        host = (String)properties.get("mail.smtp.host");
        port = (Integer)properties.get("mail.smtp.port");
    }

    private void submit(final InternetAddress from, final Iterable<InternetAddress> to, final Iterable<InternetAddress> cc, final String subject,
            final String body, final Iterable<MimeBodyPart> attachments, final Runnable postSendAction) {
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
                    msg.setHeader("X-Mailer", "Sarariman");
                    msg.setHeader("Auto-Submitted", "auto-generated");
                    msg.setHeader("X-Auto-Response-Suppress", "OOF, DR, RN, NRN");

                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText(body);

                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(messageBodyPart);

                    if (attachments != null) {
                        for (MimeBodyPart mbp : attachments) {
                            multipart.addBodyPart(mbp);
                        }
                    }

                    msg.setContent(multipart);
                    msg.setSentDate(new Date());
                    msg.saveChanges();
                    if (inhibit) {
                        System.err.println("Sending of email is inhibited.  Would have sent subject=\"" + msg.getSubject() + "\" to " +
                                Arrays.asList(msg.getAllRecipients()) + ".");
                    } else {
                        Transport transport = session.getTransport("smtp");
                        transport.connect(host, port, null, null);
                        transport.sendMessage(msg, msg.getAllRecipients());
                        transport.close();
                        if (postSendAction != null) {
                            try {
                                postSendAction.run();
                            } catch (Throwable t) {
                                logger.log(Level.SEVERE, "caught exception executing post send action", t);
                            }
                        }
                    }
                } catch (MessagingException me) {
                    logger.log(Level.SEVERE, "caught exception trying to send email", me);
                }
            }

        });
    }

    public void send(Iterable<InternetAddress> to, Iterable<InternetAddress> cc, String subject, String body) {
        submit(defaultFrom, to, cc, subject, body, null, null);
    }

    public void send(InternetAddress to, Iterable<InternetAddress> cc, String subject, String body) {
        submit(defaultFrom, Collections.singleton(to), cc, subject, body, null, null);
    }

    public void send(Iterable<InternetAddress> to, Iterable<InternetAddress> cc, String subject, String body,
            Iterable<MimeBodyPart> attachments) {
        submit(defaultFrom, to, cc, subject, body, attachments, null);
    }

    public void send(InternetAddress to, Iterable<InternetAddress> cc, String subject, String body,
            Iterable<MimeBodyPart> attachments) {
        submit(defaultFrom, Collections.singleton(to), cc, subject, body, attachments, null);
    }

    public void send(InternetAddress from, Iterable<InternetAddress> to, Iterable<InternetAddress> cc, String subject, String body) {
        submit(from, to, cc, subject, body, null, null);
    }

    public void send(InternetAddress from, InternetAddress to, Iterable<InternetAddress> cc, String subject, String body) {
        submit(from, Collections.singleton(to), cc, subject, body, null, null);
    }

    public void send(InternetAddress from, Iterable<InternetAddress> to, Iterable<InternetAddress> cc, String subject, String body,
            Iterable<MimeBodyPart> attachments) {
        submit(from, to, cc, subject, body, attachments, null);
    }

    public void send(InternetAddress from, Iterable<InternetAddress> to, Iterable<InternetAddress> cc, String subject, String body,
            Iterable<MimeBodyPart> attachments, Runnable postSendAction) {
        submit(from, to, cc, subject, body, attachments, postSendAction);
    }

    public void send(InternetAddress from, InternetAddress to, Iterable<InternetAddress> cc, String subject, String body,
            Iterable<MimeBodyPart> attachments) {
        submit(from, Collections.singleton(to), cc, subject, body, attachments, null);
    }

    public void send(InternetAddress from, InternetAddress to, Iterable<InternetAddress> cc, String subject, String body,
            Iterable<MimeBodyPart> attachments, Runnable postSendAction) {
        submit(from, Collections.singleton(to), cc, subject, body, attachments, postSendAction);
    }

    public static Iterable<InternetAddress> addresses(Iterable<Employee> employees) {
        Function<Employee, InternetAddress> f = ReflectionUtils.functionForProperty("email");
        return Iterables.transform(employees, f);
    }

}
