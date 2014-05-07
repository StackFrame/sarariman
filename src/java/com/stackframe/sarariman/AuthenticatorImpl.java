/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.logging.Logger;

/**
 *
 * @author mcculley
 */
public class AuthenticatorImpl implements Authenticator {

    private final Authenticator stackFrameAuthenticator;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public AuthenticatorImpl(Authenticator stackFrameAuthenticator) {
        this.stackFrameAuthenticator = stackFrameAuthenticator;
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        int domainIndex = username.indexOf('@');
        if (domainIndex != -1) {
            String domain = username.substring(domainIndex + 1);
            if (!"stackframe.com".equals(domain)) {
                // FIXME: Here is where we would dispatch to validate external users.
                logger.warning("Attempted log in of non-StackFrame resource. domain=" + domain);
                return false;
            }

            username = username.substring(0, domainIndex);
        }

        return stackFrameAuthenticator.checkCredentials(username, password);
    }

}
