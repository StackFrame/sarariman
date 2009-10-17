/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * A ContextListener which does things necessary when Sarariman starts up and shuts down.
 *
 * @author mcculley
 */
public class SararimanContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        EmailDispatcher emailDispatcher = new EmailDispatcher("mail.stackframe.com", 587, "sarariman@stackframe.com");
        Sarariman sarariman = new Sarariman(emailDispatcher);
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("sarariman", sarariman);
        servletContext.setAttribute("directory", sarariman.getDirectory());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        Sarariman sarariman = (Sarariman)sce.getServletContext().getAttribute("sarariman");
        if (sarariman != null) {
            sarariman.shutdown();
        }
    }

}
