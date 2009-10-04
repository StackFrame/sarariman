package com.stackframe.sarariman;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final Logger logger = Logger.getLogger(getClass().getName());
    private Sarariman sarariman;

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
        return "1.0.11r" + getRevision();
    }

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

    private void scheduleTasks(EmailDispatcher emailDispatcher, final LDAPDirectory directory, Timer timer) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        final long ONE_SECOND = 1000;
        final long ONE_MINUTE = 60 * ONE_SECOND;
        final long ONE_HOUR = 60 * ONE_MINUTE;
        final long ONE_DAY = 24 * ONE_HOUR;

        final TimerTask weeknightTask = new WeeknightTask(directory, emailDispatcher);
        timer.scheduleAtFixedRate(weeknightTask, date, ONE_DAY);

        final TimerTask reloadLDAP = new TimerTask() {

            public void run() {
                directory.reload();
            }

        };
        timer.schedule(reloadLDAP, ONE_HOUR, ONE_HOUR);
    }

    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("sararimanVersion", version());
        LDAPDirectory directory;
        try {
            Properties props = lookupDirectoryProperties();
            directory = new LDAPDirectory(new InitialDirContext(props));
            sce.getServletContext().setAttribute("directory", directory);
        } catch (NamingException ne) {
            throw new RuntimeException(ne);  // FIXME: Is this the best thing to throw here?
        }

        EmailDispatcher emailDispatcher = new EmailDispatcher("mail.stackframe.com", 587, "sarariman@stackframe.com");
        sarariman = new Sarariman(directory, emailDispatcher);
        sce.getServletContext().setAttribute("sarariman", sarariman);
        scheduleTasks(emailDispatcher, directory, sarariman.getTimer());
        /*
        try {
        emailDispatcher.send(new InternetAddress("mcculley@stackframe.com", true), "Sarariman started", "Sarariman has been started.");
        } catch (AddressException ae) {
        throw new RuntimeException(ae);
        }
         */
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (sarariman != null) {
                sarariman.getConnection().close();
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "exception while closing connection", e);
        }
        sarariman.getTimer().cancel();
    }

}
