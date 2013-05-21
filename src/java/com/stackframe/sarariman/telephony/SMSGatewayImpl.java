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
import java.util.Map;

/**
 *
 * @author mcculley
 */
public class SMSGatewayImpl implements SMSGateway {

    private final String AccountSID;

    private final String AuthToken;

    private final String from;

    private final boolean inhibit;

    public SMSGatewayImpl(String AccountSID, String AuthToken, String from, boolean inhibit) {
        this.AccountSID = AccountSID;
        this.AuthToken = AuthToken;
        this.from = from;
        this.inhibit = inhibit;
    }

    public void send(String to, String body) throws Exception {
        TwilioRestClient client = new TwilioRestClient(AccountSID, AuthToken);
        Account account = client.getAccount();
        SmsFactory smsFactory = account.getSmsFactory();
        Map<String, String> smsParams = new HashMap<String, String>();
        smsParams.put("To", to);
        smsParams.put("From", from);
        smsParams.put("Body", body);
        System.err.println("Sending SMS to " + to + ". body='" + body + "'");
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

}
