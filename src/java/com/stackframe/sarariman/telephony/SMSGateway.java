/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 *
 * @author mcculley
 */
public interface SMSGateway {

    void send(PhoneNumber to, String body) throws Exception;

    void addSMSListener(SMSListener l);

    void removeSMSListener(SMSListener l);

}
