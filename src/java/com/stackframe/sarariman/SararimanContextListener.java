/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
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

    private static Properties lookupDirectoryProperties() throws NamingException {
        Properties props = new Properties();
        Context initContext = new InitialContext();
        Context envContext = (Context)initContext.lookup("java:comp/env");
        String[] propNames = new String[]{Context.INITIAL_CONTEXT_FACTORY, Context.PROVIDER_URL, Context.SECURITY_AUTHENTICATION,
            Context.SECURITY_PRINCIPAL, Context.SECURITY_CREDENTIALS};

        for (String s : propNames) {
            props.put(s, envContext.lookup(s));
        }

        return props;
    }

    public void contextInitialized(ServletContextEvent sce) {
        LDAPDirectory directory;
        try {
            Properties props = lookupDirectoryProperties();
            directory = new LDAPDirectory(new InitialDirContext(props));
            sce.getServletContext().setAttribute("directory", directory);
        } catch (NamingException ne) {
            throw new RuntimeException(ne);  // FIXME: Is this the best thing to throw here?
        }

        EmailDispatcher emailDispatcher = new EmailDispatcher("mail.stackframe.com", 587, "sarariman@stackframe.com");
        sce.getServletContext().setAttribute("sarariman", new Sarariman(directory, emailDispatcher));
    }

    public void contextDestroyed(ServletContextEvent sce) {
        Sarariman sarariman = (Sarariman)sce.getServletContext().getAttribute("sarariman");
        if (sarariman != null) {
            sarariman.shutdown();
        }
    }

}
