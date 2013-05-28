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
import java.util.concurrent.Executor;

/**
 *
 * @author mcculley
 */
public class SMSXMPPGateway extends AbstractIdleService {

    private final SMSGateway sms;

    private final XMPPServer xmpp;

    private final Directory directory;

    private final Executor executor;

    private Employee findEmployee(PhoneNumber number) {
        for (Employee e : directory.getEmployees()) {
            PhoneNumber mobile = e.getMobile();
            if (mobile != null && mobile.equals(number)) {
                return e;
            }
        }

        return null;
    }

    private final SMSListener listener = new SMSListener() {
        public void received(SMSEvent e) {
            System.err.println("Received an SMSEvent: " + e);
            String[] words = e.getBody().split(" ");
            String firstWord = words[0].toLowerCase();
            if (ShowType.isValid(firstWord)) {
                ShowType showType = ShowType.valueOf(firstWord);
                String status = e.getBody().substring(firstWord.length() + 1);
                System.err.println("showType=" + showType + " status='" + status + "'");
                Employee from = findEmployee(e.getFrom());
                if (from == null) {
                    System.err.println("Could not find employee for number=" + e.getFrom());
                } else {
                    System.err.println("message was from " + from.getUserName());
                    PresenceType presenceType;
                    if (showType == ShowType.chat) {
                        presenceType = PresenceType.available;
                    } else {
                        presenceType = PresenceType.unavailable;
                    }

                    final Presence presence = new Presence(presenceType, showType, status);
                    final String JID = from.getUserName() + "@stackframe.com";
                    System.err.println("setting presence for " + JID + " to " + presence);
                    executor.execute(new Runnable() {
                        public void run() {
                            try {
                                xmpp.setPresence(JID, presence);
                            } catch (Throwable t) {
                                System.err.println("Trouble setting presence: " + t);
                                t.printStackTrace();
                                // FIXME: log
                            }
                        }

                    });
                }
            }
        }

    };

    public SMSXMPPGateway(SMSGateway sms, XMPPServer xmpp, Directory directory, Executor executor) {
        this.sms = sms;
        this.xmpp = xmpp;
        this.directory = directory;
        this.executor = executor;
    }

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
        return executor();
    }

}
