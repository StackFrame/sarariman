/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony.twilio;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stackframe.sarariman.Sarariman;
import com.stackframe.sarariman.telephony.SMSEvent;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioUtils;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

/**
 *
 * @author mcculley
 */
public class IncomingTwilioSMSHandler extends HttpServlet {

    private TwilioSMSGatewayImpl gateway;

    @Override
    public void init() throws ServletException {
        super.init();
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        gateway = (TwilioSMSGatewayImpl)sarariman.getSMSGateway();
    }

    private static Map<String, String> parameters(HttpServletRequest request) {
        Map<String, String> m = new HashMap<String, String>();

        // FIXME: When stepping up to more modern servlet spec, use getParameterMap().
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String[] values = request.getParameterValues(name);
            for (String value : values) {
                m.put(name, value);
            }
        }

        return m;
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = HttpUtils.getRequestURL(request).toString();
        String accountSid = request.getParameter("AccountSid");
        TwilioRestClient client = gateway.getRestClient();
        String expectedAccountSid = client.getAccountSid();
        if (!expectedAccountSid.equals(accountSid)) {
            throw new ServletException("account ID does not match");
        }

        TwilioUtils twilioUtils = new TwilioUtils(client.getAccount().getAuthToken(), expectedAccountSid);
        boolean valid = twilioUtils.validateRequest(request.getHeader("x-twilio-signature"), url, parameters(request));
        if (!valid) {
            throw new ServletException("signature does not match");
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String body = request.getParameter("Body");
        String status = request.getParameter("SmsStatus");
        long now = System.currentTimeMillis();
        try {
            PhoneNumber from = phoneNumberUtil.parse(request.getParameter("From"), "US");
            PhoneNumber to = phoneNumberUtil.parse(request.getParameter("To"), "US");
            SMSEvent e = new SMSEvent(from, to, body, now, status);
            gateway.distribute(e);
        } catch (NumberParseException npe) {
            throw new ServletException(npe);
        }
    }

}
