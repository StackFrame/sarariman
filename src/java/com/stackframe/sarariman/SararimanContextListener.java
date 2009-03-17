package com.stackframe.sarariman;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author mcculley
 */
public class SararimanContextListener implements ServletContextListener {

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
        return "1.0.2r" + getRevision();
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("sararimanVersion", version());
    }

}