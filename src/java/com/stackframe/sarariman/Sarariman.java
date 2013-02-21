/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.stackframe.reflect.ReflectionUtils;
import com.stackframe.sarariman.tickets.Ticket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class Sarariman implements ServletContextListener, ConnectionFactory {

    private final Collection<Employee> approvers = new EmployeeTable(this, "approvers");
    private final Collection<Employee> invoiceManagers = new EmployeeTable(this, "invoice_managers");
    private final Collection<Employee> timesheetManagers = new EmployeeTable(this, "timesheet_managers");
    private final Collection<LaborCategoryAssignment> projectBillRates = new LaborCategoryAssignmentTable(this);
    private final Collection<LaborCategory> laborCategories = new LaborCategoryTable(this);
    private final Collection<Extension> extensions = new ArrayList<Extension>();
    private final Holidays holidays = new HolidaysImpl(this);
    private final DirectorySynchronizer directorySynchronizer = new DirectorySynchronizerImpl();
    private OrganizationHierarchy organizationHierarchy;
    private LDAPDirectory directory;
    private EmailDispatcher emailDispatcher;
    private CronJobs cronJobs;
    private String logoURL;
    private String mountPoint;

    public String getVersion() {
        return Version.version;
    }

    private static Properties lookupDirectoryProperties(Context envContext) throws NamingException {
        Properties props = new Properties();
        String[] propNames = new String[]{Context.INITIAL_CONTEXT_FACTORY, Context.PROVIDER_URL, Context.SECURITY_AUTHENTICATION,
            Context.SECURITY_PRINCIPAL, Context.SECURITY_CREDENTIALS};

        for (String s : propNames) {
            props.put(s, envContext.lookup(s));
        }

        return props;
    }

    private static Properties lookupMailProperties(Context envContext) throws NamingException {
        Properties props = new Properties();
        String[] propNames = new String[]{"mail.from", "mail.smtp.host", "mail.smtp.port"};

        for (String s : propNames) {
            props.put(s, envContext.lookup(s));
        }

        return props;
    }

    public DataSource getDataSource() {
        try {
            return (DataSource)new InitialContext().lookup("java:comp/env/jdbc/sarariman");
        } catch (NamingException namingException) {
            throw new RuntimeException(namingException);
        }
    }

    public Connection openConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public EmailDispatcher getEmailDispatcher() {
        return emailDispatcher;
    }

    public Collection<Employee> getApprovers() {
        return approvers;
    }

    public Collection<Employee> getInvoiceManagers() {
        return invoiceManagers;
    }

    public Collection<Employee> getTimesheetManagers() {
        return timesheetManagers;
    }

    public Map<Long, Customer> getCustomers() throws SQLException {
        return Customer.getCustomers(this);
    }

    public Map<Long, Project> getProjects() throws SQLException {
        return Project.getProjects(this);
    }

    public Collection<Task> getTasks() throws SQLException {
        return Task.getTasks(this);
    }

    public Collection<Employee> getAdministrators() {
        Predicate<Employee> isAdministrator = ReflectionUtils.predicateForProperty(Employee.class, "administrator");
        return Collections2.filter(directory.getByUserName().values(), isAdministrator);
    }

    public String getLogoURL() {
        return logoURL;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public Collection<LaborCategoryAssignment> getProjectBillRates() {
        return projectBillRates;
    }

    public Map<Long, LaborCategory> getLaborCategories() {
        Map<Long, LaborCategory> result = new LinkedHashMap<Long, LaborCategory>();
        for (LaborCategory lc : laborCategories) {
            result.put(lc.getId(), lc);
        }

        return result;
    }

    public Collection<Extension> getExtensions() {
        return extensions;
    }

    public OrganizationHierarchy getOrganizationHierarchy() {
        return organizationHierarchy;
    }

    public Collection<Employee> employees(Collection<Integer> ids) {
        Collection<Employee> result = new ArrayList<Employee>();
        for (int id : ids) {
            result.add(directory.getByNumber().get(id));
        }

        return result;
    }

    public Holidays getHolidays() {
        return holidays;
    }

    public Collection<Audit> getGlobalAudits() {
        Collection<Audit> c = new ArrayList<Audit>();
        c.add(new OrgChartGlobalAudit(this));
        c.add(new ContactsGlobalAudit(this));
        c.add(new DirectRateAudit(directory));
        return c;
    }

    public boolean isBoss(Employee employee) throws SQLException {
        Collection<Integer> bossIDs = organizationHierarchy.getBosses();
        return bossIDs.contains(employee.getNumber());
    }

    public static boolean isBoss(Sarariman sarariman, Employee employee) throws SQLException {
        return sarariman.isBoss(employee);
    }

    /**
     * Make contains visible to the tag library.
     *
     * @param coll
     * @param o
     * @return whether or not coll contains o
     */
    public static boolean contains(Collection<?> coll, Object o) {
        return coll.contains(o);
    }

    DirectorySynchronizer getDirectorySynchronizer() {
        return directorySynchronizer;
    }

    public URL getTicketURL(int ticketID) {
        try {
            return new URL(getMountPoint() + "tickets/" + ticketID);
        } catch (MalformedURLException mue) {
            throw new AssertionError(mue);
        }
    }

    public URL getTicketURL(Ticket ticket) {
        return getTicketURL(ticket.getId());
    }

    public void contextInitialized(ServletContextEvent sce) {
        extensions.add(new SAICExtension());
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context)initContext.lookup("java:comp/env");
            Properties directoryProperties = lookupDirectoryProperties(envContext);
            directory = new LDAPDirectory(new InitialDirContext(directoryProperties), this);
            try {
                directorySynchronizer.synchronize(directory, this);
            } catch (Exception e) {
                // FIXME: log
                System.err.println("Trouble synchronizing directory with database: " + e);
            }

            initContext.rebind("sarariman.directory", directory);
            organizationHierarchy = new OrganizationHierarchyImpl(this, directory);
            boolean inhibitEmail = (Boolean)envContext.lookup("inhibitEmail");
            emailDispatcher = new EmailDispatcher(lookupMailProperties(envContext), inhibitEmail);
            logoURL = (String)envContext.lookup("logoURL");
            mountPoint = (String)envContext.lookup("mountPoint");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);  // FIXME: Is this the best thing to throw here?
        }

        cronJobs = new CronJobs(this, directory, emailDispatcher);

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("sarariman", this);
        servletContext.setAttribute("directory", directory);

        cronJobs.start();
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            hostname = "unknown host";
        }

        for (Employee employee : getAdministrators()) {
            String message = String.format("Sarariman version %s has been started on %s at %s.", getVersion(), hostname, mountPoint);
            emailDispatcher.send(employee.getEmail(), null, "sarariman started", message);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // FIXME: Should we worry about email that has been queued but not yet sent?
        cronJobs.stop();
    }

}
