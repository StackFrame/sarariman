/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stackframe.sarariman.telephony.SMSEvent;
import com.stackframe.sarariman.telephony.SMSGateway;
import com.stackframe.sarariman.telephony.SMSListener;
import com.stackframe.sarariman.xmpp.Presence;
import com.stackframe.sarariman.xmpp.PresenceType;
import com.stackframe.sarariman.xmpp.ShowType;
import com.stackframe.sarariman.xmpp.XMPPServer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 *
 * @author mcculley
 */
public class SMSXMPPGateway extends AbstractIdleService {

    private final SMSGateway sms;

    private final XMPPServer xmpp;

    private final Directory directory;

    private final Executor executor;

    private final DataSource dataSource;

    private final Executor backgroundDatabaseWriteExecutor;

    private final Logger logger = Logger.getLogger(getClass());

    public SMSXMPPGateway(SMSGateway sms, XMPPServer xmpp, Directory directory, Executor executor, DataSource dataSource,
                          Executor backgroundDatabaseWriteExecutor) {
        this.sms = sms;
        this.xmpp = xmpp;
        this.directory = directory;
        this.executor = executor;
        this.dataSource = dataSource;
        this.backgroundDatabaseWriteExecutor = backgroundDatabaseWriteExecutor;
    }

    private Employee findEmployee(PhoneNumber number) {
        for (Employee e : directory.getEmployees()) {
            PhoneNumber mobile = e.getMobile();
            if (mobile != null && mobile.equals(number)) {
                return e;
            }
        }

        return null;
    }

    private static String stripPunctuation(String s) {
        StringBuilder buf = new StringBuilder();
        int numCharacters = s.length();
        for (int i = 0; i < numCharacters; i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c) || Character.isLetter(c)) {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    private void writeToLog(String entity, Presence presence, long timestamp) {
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement(
                        "INSERT INTO presence_log (entity, type, `show`, message, `timestamp`) " +
                        "VALUES(?, ?, ?, ?, ?)");
                try {
                    s.setString(1, entity.toString());
                    s.setString(2, presence.getType().name());
                    s.setString(3, presence.getShow().name());
                    s.setString(4, presence.getStatus());
                    s.setTimestamp(5, new Timestamp(timestamp));
                    int numRowsInserted = s.executeUpdate();
                    assert numRowsInserted == 1;
                } finally {
                    s.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            logger.error("trouble writing to presence_log", e);
        }
    }

    private final SMSListener listener = new SMSListener() {
        public void received(SMSEvent e) {
            logger.info("Received an SMSEvent: " + e);
            String[] words = e.getBody().split(" ");
            String firstWord = words[0];

            String state = stripPunctuation(firstWord.toLowerCase());

            if (ShowType.isValid(state)) {
                ShowType showType = ShowType.valueOf(state);
                String status = e.getBody().substring(firstWord.length() + 1);
                System.err.println("showType=" + showType + " status='" + status + "'");
                Employee from = findEmployee(e.getFrom());
                if (from == null) {
                    logger.warn("Could not find employee for number=" + e.getFrom());
                } else {
                    logger.info("message was from " + from.getUserName());

                    // I'm not completely sure this is the right thing to do, but if we don't set it to available, nobody will see
                    // the message, I think.
                    PresenceType presenceType = PresenceType.available;

                    final Presence presence = new Presence(presenceType, showType, status);
                    final String JID = from.getUserName() + "@stackframe.com";
                    final long now = System.currentTimeMillis();
                    logger.info("setting presence for " + JID + " to " + presence);
                    executor.execute(new Runnable() {
                        public void run() {
                            try {
                                xmpp.setPresence(JID, presence);
                            } catch (Throwable t) {
                                logger.error("trouble setting presence", t);
                            }
                        }

                    });
                    backgroundDatabaseWriteExecutor.execute(new Runnable() {
                        public void run() {
                            writeToLog(JID, presence, now);
                        }

                    });
                }
            }
        }

    };

    @Override
    protected void startUp() throws Exception {
        sms.addSMSListener(listener);
    }

    @Override
    protected void shutDown() throws Exception {
        sms.removeSMSListener(listener);
    }

    @Override
    protected Executor executor() {
        return executor;
    }

    @Override
    protected String serviceName() {
        return "SMS XMPP Gateway";
    }

}
