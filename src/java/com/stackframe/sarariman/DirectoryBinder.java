package com.stackframe.sarariman;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Binds to the LDAP directory as soon as the session as created.
 *
 * @author mcculley
 */
public class DirectoryBinder implements HttpSessionListener {

    private Properties lookupDirectoryProperties() throws NamingException {
        Properties props = new Properties();
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:comp/env");
        String[] propNames = new String[]{Context.INITIAL_CONTEXT_FACTORY, Context.PROVIDER_URL, Context.SECURITY_AUTHENTICATION,
            Context.SECURITY_PRINCIPAL, Context.SECURITY_CREDENTIALS
        };

        for (String s : propNames) {
            props.put(s, envContext.lookup(s));
        }

        return props;
    }

    public void sessionCreated(HttpSessionEvent evt) {
        HttpSession session = evt.getSession();
        try {
            Properties props = lookupDirectoryProperties();
            session.setAttribute("directory", new LDAPDirectory(new InitialDirContext(props)));
        } catch (NamingException ne) {
            throw new RuntimeException(ne);  // FIXME: Is this the best thing to throw here?
        }
    }

    public void sessionDestroyed(HttpSessionEvent evt) {
    }

}