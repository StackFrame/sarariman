package com.stackframe.sarariman;

import javax.mail.internet.InternetAddress;

/**
 *
 * @author mcculley
 */
public interface Employee {

    String getUserName();

    String getFullName();

    int getNumber();

    boolean isFulltime();

    InternetAddress getEmail();

}
