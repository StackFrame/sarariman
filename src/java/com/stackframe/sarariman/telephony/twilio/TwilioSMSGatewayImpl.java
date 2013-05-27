/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony.twilio;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stackframe.sarariman.telephony.SMSEvent;
import com.stackframe.sarariman.telephony.SMSGateway;
import com.stackframe.sarariman.telephony.SMSListener;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Sms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class TwilioSMSGatewayImpl implements SMSGateway {

    private final TwilioRestClient client;

    private final PhoneNumber from;

    private final boolean inhibit;

    private final Executor databaseExecutor;

    private final DataSource dataSource;

    private final List<SMSListener> listeners = new CopyOnWriteArrayList<SMSListener>();

    public TwilioSMSGatewayImpl(TwilioRestClient client, PhoneNumber from, boolean inhibit, Executor databaseExecutor, DataSource dataSource) {
        this.client = client;
        this.from = from;
        this.inhibit = inhibit;
        this.databaseExecutor = databaseExecutor;
        this.dataSource = dataSource;
    }

    TwilioRestClient getRestClient() {
        return client;
    }

    public void send(PhoneNumber to, String body) throws Exception {
        Account account = client.getAccount();
        SmsFactory smsFactory = account.getSmsFactory();
        Map<String, String> smsParams = new HashMap<String, String>();
        smsParams.put("To", format(to));
        smsParams.put("From", format(from));
        smsParams.put("Body", body);
        if (inhibit) {
            System.err.println("Sending of SMS inhibited. Would have sent body='" + body + "' to " + format(to));
        } else {
            try {
                long now = System.currentTimeMillis();
                Sms sms = smsFactory.create(smsParams);
                log(new SMSEvent(from, to, body, now, sms.getStatus()));
            } catch (TwilioRestException tre) {
                throw new Exception(tre);
            }
        }
    }

    public void addSMSListener(SMSListener l) {
        listeners.add(l);
    }

    public void removeSMSListener(SMSListener l) {
        listeners.remove(l);
    }

    private static String format(PhoneNumber phoneNumber) {
        return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    private void log(final SMSEvent e) {
        Runnable insertTask = new Runnable() {
            public void run() {
                try {
                    Connection c = dataSource.getConnection();
                    try {
                        PreparedStatement s = c.prepareStatement(
                                "INSERT INTO sms_log (`from`, `to`, body, `timestamp`, status) " +
                                "VALUES(?, ?, ?, ?, ?)");
                        try {
                            s.setString(1, format(e.getFrom()));
                            s.setString(2, format(e.getTo()));
                            s.setString(3, e.getBody());
                            s.setTimestamp(4, new Timestamp(e.getTimestamp()));
                            s.setString(5, e.getStatus());
                            int numRowsInserted = s.executeUpdate();
                            assert numRowsInserted == 1;
                        } finally {
                            s.close();
                        }
                    } finally {
                        c.close();
                    }
                } catch (SQLException e) {
                    // FIXME: Should we log this exception? Does it kill the Executor?
                    throw new RuntimeException(e);
                }
            }

        };
        databaseExecutor.execute(insertTask);
    }

    public void distribute(SMSEvent e) {
        log(e);
        for (SMSListener l : listeners) {
            l.received(e);
        }
    }

}
