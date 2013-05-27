/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class AuthenticatorImpl implements Authenticator {

    private final Authenticator stackFrameAuthenticator;

    public AuthenticatorImpl(Authenticator stackFrameAuthenticator) {
        this.stackFrameAuthenticator = stackFrameAuthenticator;
    }

    public boolean checkCredentials(String username, String password) {
        int domainIndex = username.indexOf('@');
        if (domainIndex != -1) {
            String domain = username.substring(domainIndex + 1);
            if (!"stackframe.com".equals(domain)) {
                // FIXME: Here is where we would dispatch to validate external users.
                System.err.println("Attempted log in of non-StackFrame resource. domain=" + domain);
                return false;
            }

            username = username.substring(0, domainIndex);
        }

        return stackFrameAuthenticator.checkCredentials(username, password);
    }

}
