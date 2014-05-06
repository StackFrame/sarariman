/*
 * Copyright (C) 2009-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import static com.google.common.base.Preconditions.*;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.stackframe.reflect.ReflectionUtils;
import com.stackframe.sarariman.accesslog.AccessLog;
import com.stackframe.sarariman.accesslog.AccessLogImpl;
import com.stackframe.sarariman.clients.Clients;
import com.stackframe.sarariman.clients.ClientsImpl;
import com.stackframe.sarariman.conference.Conferences;
import com.stackframe.sarariman.conference.ConferencesImpl;
import com.stackframe.sarariman.contacts.Contacts;
import com.stackframe.sarariman.contacts.ContactsImpl;
import com.stackframe.sarariman.errors.Errors;
import com.stackframe.sarariman.errors.ErrorsImpl;
import com.stackframe.sarariman.events.Events;
import com.stackframe.sarariman.events.EventsImpl;
import com.stackframe.sarariman.holidays.Holidays;
import com.stackframe.sarariman.holidays.HolidaysImpl;
import com.stackframe.sarariman.invoices.Credits;
import com.stackframe.sarariman.invoices.CreditsImpl;
import com.stackframe.sarariman.invoices.Invoices;
import com.stackframe.sarariman.invoices.InvoicesImpl;
import com.stackframe.sarariman.locationlog.LocationLog;
import com.stackframe.sarariman.locationlog.LocationLogImpl;
import com.stackframe.sarariman.logincookies.LoginCookies;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntries;
import com.stackframe.sarariman.outofoffice.OutOfOfficeEntriesImpl;
import com.stackframe.sarariman.projects.LaborProjections;
import com.stackframe.sarariman.projects.LaborProjectionsImpl;
import com.stackframe.sarariman.projects.Projects;
import com.stackframe.sarariman.projects.ProjectsImpl;
import com.stackframe.sarariman.taskassignments.DefaultTaskAssignments;
import com.stackframe.sarariman.taskassignments.DefaultTaskAssignmentsImpl;
import com.stackframe.sarariman.taskassignments.TaskAssignments;
import com.stackframe.sarariman.taskassignments.TaskAssignmentsImpl;
import com.stackframe.sarariman.tasks.Tasks;
import com.stackframe.sarariman.tasks.TasksImpl;
import com.stackframe.sarariman.telephony.SMSGateway;
import com.stackframe.sarariman.telephony.twilio.TwilioSMSGatewayImpl;
import com.stackframe.sarariman.tickets.Tickets;
import com.stackframe.sarariman.tickets.TicketsImpl;
import com.stackframe.sarariman.timesheets.Timesheets;
import com.stackframe.sarariman.timesheets.TimesheetsImpl;
import com.stackframe.sarariman.vacation.Vacations;
import com.stackframe.sarariman.vacation.VacationsImpl;
import com.stackframe.sarariman.xmpp.XMPPServer;
import com.stackframe.sarariman.xmpp.vysper.VysperXMPPServer;
import com.twilio.sdk.TwilioRestClient;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 *
 * @author mcculley
 */
public class Sarariman implements ServletContextListener {

    private final Logger logger = Logger.getLogger(getClass());

    // This ExecutorService is used for background jobs which do not require synchronous completion with regard to an HTTP request
    // and for periodic background tasks.
    private final ScheduledThreadPoolExecutor backgroundExecutor = new ScheduledThreadPoolExecutor(1);

    // This ExecutorService is used for background jobs which write to the database and do not require synchronous completion with
    // regard to an HTTP request.
    private final ExecutorService backgroundDatabaseWriteExecutor = Executors.newFixedThreadPool(1);

    private final Collection<Employee> approvers = new EmployeeTable(this, "approvers");

    private final Collection<Employee> invoiceManagers = new EmployeeTable(this, "invoice_managers");

    private final Collection<Employee> timesheetManagers = new EmployeeTable(this, "timesheet_managers");

    private final Collection<LaborCategoryAssignment> projectBillRates = new LaborCategoryAssignmentTable(this);

    private final Collection<LaborCategory> laborCategories = new LaborCategoryTable(this);

    private final Collection<Extension> extensions = new ArrayList<>();

    private final Holidays holidays = new HolidaysImpl(getDataSource());

    private final DirectorySynchronizer directorySynchronizer = new DirectorySynchronizerImpl();

    private OrganizationHierarchy organizationHierarchy;

    private LDAPDirectory directory;

    private EmailDispatcher emailDispatcher;

    private CronJobs cronJobs;

    private String logoURL;

    private String mountPoint;

    private TimesheetEntries timesheetEntries;

    private Projects projects;

    private Tasks tasks;

    private Clients clients;

    private Tickets tickets;

    private Events events;

    private Vacations vacations;

    private OutOfOfficeEntries outOfOffice;

    private Contacts contacts;

    private Timesheets timesheets;

    private Errors errors;

    private AccessLog accessLog;

    private Workdays workdays;

    private PaidTimeOff paidTimeOff;

    private LaborProjections laborProjections;

    private TaskAssignments taskAssignments;

    private DefaultTaskAssignments defaultTaskAssignments;

    private LoginCookies loginCookies;

    private LocationLog locationLog;

    private SMSGateway SMS;

    private XMPPServer xmpp;

    private final Collection<Service> services = new CopyOnWriteArrayList<>();

    private ServiceManager serviceManager;

    private Conferences conferences;

    private Invoices invoices;

    private Credits credits;

    // FIXME: Need a web UI under DevOps to adjust this.
    private Level logLevel = Level.INFO;

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

    public Timesheets getTimesheets() {
        return timesheets;
    }

    public Clients getClients() {
        return clients;
    }

    private Collection<Employee> getAdministrators() {
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
        // FIXME: Can replace with Maps.asMap?
        Map<Long, LaborCategory> result = new LinkedHashMap<>();
        laborCategories.stream().forEach((lc) -> {
            result.put(lc.getId(), lc);
        });

        return result;
    }

    public Collection<Extension> getExtensions() {
        return extensions;
    }

    public AccessLog getAccessLog() {
        return accessLog;
    }

    public OrganizationHierarchy getOrganizationHierarchy() {
        return organizationHierarchy;
    }

    public TaskAssignments getTaskAssignments() {
        return taskAssignments;
    }

    public Collection<Employee> employees(Collection<Integer> ids) {
        return ids.stream().map(directory.getByNumber()::get).collect(Collectors.toList());
    }

    public Holidays getHolidays() {
        return holidays;
    }

    public Vacations getVacations() {
        return vacations;
    }

    public LaborProjections getLaborProjections() {
        return laborProjections;
    }

    public DefaultTaskAssignments getDefaultTaskAssignments() {
        return defaultTaskAssignments;
    }

    public LoginCookies getLoginCookies() {
        return loginCookies;
    }

    public LocationLog getLocationLog() {
        return locationLog;
    }

    public Invoices getInvoices() {
        return invoices;
    }

    public Credits getCredits() {
        return credits;
    }

    public Collection<Audit> getGlobalAudits() {
        Collection<Audit> c = new ArrayList<>();
        c.add(new OrgChartGlobalAudit(this));
        c.add(new TimesheetAudit(this, directory));
        c.add(new ContactsGlobalAudit(getDataSource(), contacts));
        c.add(new ProjectAdministrativeAssistantGlobalAudit(getDataSource(), projects));
        c.add(new ProjectManagerGlobalAudit(getDataSource(), projects));
        c.add(new ProjectCostManagerGlobalAudit(getDataSource(), projects));
        c.add(new DirectRateAudit(directory));
        c.add(new TaskAssignmentsGlobalAudit(getDataSource(), directory, tasks, taskAssignments));
        return c;
    }

    public static boolean isBoss(OrganizationHierarchy organizationHierarchy, Employee employee) throws SQLException {
        Collection<Integer> bossIDs = organizationHierarchy.getBosses();
        return bossIDs.contains(employee.getNumber());
    }

    public boolean isBoss(Employee employee) throws SQLException {
        return isBoss(organizationHierarchy, employee);
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
        checkNotNull(coll);
        return coll.contains(o);
    }

    DirectorySynchronizer getDirectorySynchronizer() {
        return directorySynchronizer;
    }

    public ScheduledThreadPoolExecutor getTimer() {
        return backgroundExecutor;
    }

    public TimesheetEntries getTimesheetEntries() {
        return timesheetEntries;
    }

    public Projects getProjects() {
        return projects;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public Tickets getTickets() {
        return tickets;
    }

    public Events getEvents() {
        return events;
    }

    public Errors getErrors() {
        return errors;
    }

    public OutOfOfficeEntries getOutOfOfficeEntries() {
        return outOfOffice;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public Workdays getWorkdays() {
        return workdays;
    }

    public PaidTimeOff getPaidTimeOff() {
        return paidTimeOff;
    }

    public Executor getBackgroundDatabaseWriteExecutor() {
        return backgroundDatabaseWriteExecutor;
    }

    public SMSGateway getSMSGateway() {
        return SMS;
    }

    public XMPPServer getXMPPServer() {
        return xmpp;
    }

    public Collection<UIResource> getNavbarLinks() {
        return ImmutableList.<UIResource>of(new UIResourceImpl(getMountPoint(), "Home", "icon-home"));
    }

    public Conferences getConferences() {
        return conferences;
    }

    public Collection<Service> getServices() {
        return Collections.unmodifiableCollection(services);
    }

    private void setupLogger(DeploymentMode deploymentMode) {
        SLF4JBridgeHandler.install();
        if (deploymentMode == DeploymentMode.production) {
            Logger.getRootLogger().removeAllAppenders();
            final Log log = new LogImpl(getDataSource(), backgroundDatabaseWriteExecutor);
            Appender appender = new AppenderSkeleton() {
                @Override
                protected void append(LoggingEvent le) {
                    if (!le.getLevel().isGreaterOrEqual(logLevel)) {
                        return;
                    }

                    long timestamp = System.currentTimeMillis();
                    String priority = le.getLevel().toString();
                    String source = le.getLoggerName();
                    String message = le.getMessage().toString();
                    String exceptionText;
                    ThrowableInformation ti = le.getThrowableInformation();
                    if (ti == null) {
                        exceptionText = null;
                    } else {
                        Throwable t = ti.getThrowable();
                        exceptionText = t.getMessage() + "\n" + Throwables.getStackTraceAsString(t);
                    }

                    log.log(timestamp, priority, source, message, exceptionText);
                }

                @Override
                public boolean requiresLayout() {
                    return false;
                }

                @Override
                public void close() {
                }

            };
            Logger.getRootLogger().addAppender(appender);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Logger.getRootLogger().addAppender(new ConsoleAppender());
        final String applicationName = sce.getServletContext().getServletContextName();
        logger.info("context initialized for " + applicationName + " serverInfo=" + sce.getServletContext().getServerInfo());
        extensions.add(new SAICExtension());
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context)initContext.lookup("java:comp/env");
            DeploymentMode deploymentMode = DeploymentMode.valueOf((String)envContext.lookup("deploymentMode"));
            setupLogger(deploymentMode);
            Properties directoryProperties = lookupDirectoryProperties(envContext);
            directory = new LDAPDirectory(new InitialDirContext(directoryProperties), this);
            try {
                directorySynchronizer.synchronize(directory, getDataSource());
            } catch (Exception e) {
                logger.error("trouble synchronizing directory with database", e);
            }

            initContext.rebind("sarariman.directory", directory);
            organizationHierarchy = new OrganizationHierarchyImpl(getDataSource(), directory);
            boolean inhibitEmail = (Boolean)envContext.lookup("inhibitEmail");
            String twilioAccountSID = (String)envContext.lookup("twilioAccountSID");
            String twilioAuthToken = (String)envContext.lookup("twilioAuthToken");
            TwilioRestClient twilioClient = new TwilioRestClient(twilioAccountSID, twilioAuthToken);
            boolean inhibitSMS = (Boolean)envContext.lookup("inhibitSMS");
            String SMSFrom = (String)envContext.lookup("SMSFrom");
            try {
                SMS = new TwilioSMSGatewayImpl(twilioClient, PhoneNumberUtil.getInstance().parse(SMSFrom, "US"), inhibitSMS,
                                               backgroundDatabaseWriteExecutor, getDataSource());
            } catch (NumberParseException pe) {
                throw new RuntimeException(pe);
            }

            emailDispatcher = new EmailDispatcher(lookupMailProperties(envContext), inhibitEmail, backgroundExecutor);
            logoURL = (String)envContext.lookup("logoURL");
            mountPoint = (String)envContext.lookup("mountPoint");
            clients = new ClientsImpl(getDataSource(), mountPoint);
            projects = new ProjectsImpl(getDataSource(), organizationHierarchy, directory, this);
            tasks = new TasksImpl(getDataSource(), getMountPoint(), projects);
            timesheetEntries = new TimesheetEntriesImpl(getDataSource(), directory, tasks, mountPoint);
            tickets = new TicketsImpl(getDataSource(), mountPoint);
            events = new EventsImpl(getDataSource(), mountPoint);
            vacations = new VacationsImpl(getDataSource(), directory);
            outOfOffice = new OutOfOfficeEntriesImpl(getDataSource(), directory);
            contacts = new ContactsImpl(getDataSource(), mountPoint);
            timesheets = new TimesheetsImpl(this, mountPoint);
            errors = new ErrorsImpl(getDataSource(), mountPoint, directory);
            accessLog = new AccessLogImpl(getDataSource(), directory);
            workdays = new WorkdaysImpl(holidays);
            paidTimeOff = new PaidTimeOff(tasks);
            credits = new CreditsImpl(getDataSource());
            invoices = new InvoicesImpl(getDataSource(), credits);
            laborProjections = new LaborProjectionsImpl(getDataSource(), directory, tasks, mountPoint);
            taskAssignments = new TaskAssignmentsImpl(directory, getDataSource(), mountPoint);
            defaultTaskAssignments = new DefaultTaskAssignmentsImpl(getDataSource(), tasks);
            loginCookies = new LoginCookies(getDataSource(), backgroundExecutor);
            locationLog = new LocationLogImpl(getDataSource(), directory, backgroundDatabaseWriteExecutor);
            String keyStorePath = (String)envContext.lookup("keyStorePath");
            String keyStorePassword = (String)envContext.lookup("keyStorePassword");
            xmpp = new VysperXMPPServer("stackframe.com", directory, new File(keyStorePath), keyStorePassword,
                                        backgroundExecutor, getDataSource(), backgroundDatabaseWriteExecutor);
            conferences = new ConferencesImpl(xmpp);
            services.add(xmpp);
            SMSXMPPGateway gateway = new SMSXMPPGateway(SMS, xmpp, directory, backgroundExecutor, getDataSource(),
                                                        backgroundDatabaseWriteExecutor);
            services.add(gateway);
        } catch (NamingException ne) {
            throw new RuntimeException(ne);  // FIXME: Is this the best thing to throw here?
        }

        serviceManager = new ServiceManager(services);
        serviceManager.startAsync();

        cronJobs = new CronJobs(backgroundExecutor, this, directory, emailDispatcher);

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("sarariman", this);
        servletContext.setAttribute("directory", directory);

        cronJobs.start();
        final String hostname = getHostname();

        Runnable sendStartupEmailNotification = () -> {
            try {
                for (Employee employee : getAdministrators()) {
                    String message = String.format("%s version %s has been started on %s at %s.", applicationName, getVersion(),
                                                   hostname, mountPoint);
                    emailDispatcher.send(employee.getEmail(), null, "sarariman started", message);
                    SMS.send(employee.getMobile(), "Sarariman has been started.");
                }
            } catch (Exception e) {
                logger.error("trouble sending startup notification", e);
            }
        };
        backgroundExecutor.execute(sendStartupEmailNotification);
    }

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            return "unknown host";
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // FIXME: Should we worry about email that has been queued but not yet sent?
        serviceManager.stopAsync().awaitStopped();
        backgroundExecutor.shutdown();
        backgroundDatabaseWriteExecutor.shutdown();
        // FIXME: I'm pretty sure Vysper is not shutting down correctly. Maybe we need to see if any threads created from this
        // context are left over at this point.
    }

}
