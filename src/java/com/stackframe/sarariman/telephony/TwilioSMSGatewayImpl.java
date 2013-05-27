/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Sms;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author mcculley
 */
public class TwilioSMSGatewayImpl implements SMSGateway {

    private final TwilioRestClient client;

    private final String from;

    private final boolean inhibit;

    private final List<SMSListener> listeners = new CopyOnWriteArrayList<SMSListener>();

    public TwilioSMSGatewayImpl(TwilioRestClient client, String from, boolean inhibit) {
        this.client = client;
        this.from = from;
        this.inhibit = inhibit;
    }

    public void send(String to, String body) throws Exception {
        Account account = client.getAccount();
        SmsFactory smsFactory = account.getSmsFactory();
        Map<String, String> smsParams = new HashMap<String, String>();
        smsParams.put("To", to);
        smsParams.put("From", from);
        smsParams.put("Body", body);
        if (inhibit) {
            System.err.println("Sending of SMS inhibited. Would have sent body='" + body + "' to " + to);
        } else {
            try {
                Sms sms = smsFactory.create(smsParams);
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

    public void distribute(SMSEvent e) {
        for (SMSListener l : listeners) {
            l.received(e);
        }
    }

}
