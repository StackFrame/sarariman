/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public interface Authenticator {

    /**
     * Checks a username and password pair to see if it is valid.
     *
     * @param username the username to test
     * @param password the password to test
     * @return <code>true</code> if the username and password pair is valid, <code>false</code> otherwise
     */
    boolean checkCredentials(String username, String password);

}
