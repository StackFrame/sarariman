/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vcard;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 *
 * @author mcculley
 */
public class vCard3Renderer implements vCardRenderer {

    private static String escape(String s) {
        // FIXME: Are there other characters than comma which need escaping?
        StringBuilder buf = new StringBuilder();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c == ',') {
                buf.append("\\,");
            } else {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    public String render(vCardSource s) {
        StringBuilder buf = new StringBuilder();

        buf.append("BEGIN:VCARD\n");
        buf.append("VERSION:3.0\n");

        // FIXME: What to do for organizations?
        buf.append(String.format("FN:%s\n", s.getFullName()));

        buf.append(String.format("N:%s;%s\n", s.getFamilyName(), s.getGivenName()));

        buf.append(String.format("ORG:%s\n", escape(s.getOrganization())));

        buf.append(String.format("EMAIL;type=INTERNET;type=WORK:%s\n", s.getEmailAddress()));

        PhoneNumber mobile = s.getMobile();
        if (mobile != null) {
            String formattedMobile = PhoneNumberUtil.getInstance().format(mobile, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            buf.append(String.format("TEL;type=CELL:%s\n", formattedMobile));
        }

        buf.append("END:VCARD\n");

        return buf.toString();
    }

    public String render(Iterable<vCardSource> sources) {
        StringBuilder buf = new StringBuilder();
        for (vCardSource s : sources) {
            buf.append(render(s));
        }

        return buf.toString();
    }

}
