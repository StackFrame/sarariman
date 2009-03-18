package com.stackframe.sarariman;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Binds to the LDAP directory as soon as the session as created.
 *
 * @author mcculley
 */
public class DirectoryBinder implements ServletContextListener {

    private Properties lookupDirectoryProperties() throws NamingException {
        Properties props = new Properties();
        Context initContext = new InitialContext();
        Context envContext = (Context)initContext.lookup("java:comp/env");
        String[] propNames = new String[]{Context.INITIAL_CONTEXT_FACTORY, Context.PROVIDER_URL, Context.SECURITY_AUTHENTICATION,
            Context.SECURITY_PRINCIPAL, Context.SECURITY_CREDENTIALS
        };

        for (String s : propNames) {
            props.put(s, envContext.lookup(s));
        }

        return props;
    }

    public void contextInitialized(ServletContextEvent sce) {
        try {
            Properties props = lookupDirectoryProperties();
            sce.getServletContext().setAttribute("directory", new LDAPDirectory(new InitialDirContext(props)));
        } catch (NamingException ne) {
            throw new RuntimeException(ne);  // FIXME: Is this the best thing to throw here?
        }
    }

    public void contextDestroyed(ServletContextEvent evt) {
    }

}