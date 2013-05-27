/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony;

/**
 *
 * @author mcculley
 */
public interface SMSGateway {

    void send(String to, String body) throws Exception;

    void addSMSListener(SMSListener l);

    void removeSMSListener(SMSListener l);

}
