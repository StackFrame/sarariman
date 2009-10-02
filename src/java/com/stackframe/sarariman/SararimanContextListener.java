package com.stackframe.sarariman;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * A ContextListener which does things necessary when Sarariman starts up and shuts down.
 *
 * @author mcculley
 */
public class SararimanContextListener implements ServletContextListener {

    /** Do not edit this.  It is set by Subversion. */
    private final static String revision = "$Revision$";

    private static String getRevision() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < revision.length(); i++) {
            char c = revision.charAt(i);
            if (Character.isDigit(c)) {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    public static String version() {
        return "1.0.10r" + getRevision();
    }

    private static Properties lookupDirectoryProperties() throws NamingException {
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
        sce.getServletContext().setAttribute("sararimanVersion", version());
        try {
            Properties props = lookupDirectoryProperties();
            sce.getServletContext().setAttribute("directory", new LDAPDirectory(new InitialDirContext(props)));
        } catch (NamingException ne) {
            throw new RuntimeException(ne);  // FIXME: Is this the best thing to throw here?
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

}
