/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vcard;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 *
 * @author mcculley
 */
public interface vCardSource {

    String getFamilyName();

    String getGivenName();

    String getFullName();

    String getEmailAddress();

    String getOrganization();

    PhoneNumber getMobile();

}
