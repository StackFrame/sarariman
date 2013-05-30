/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp.vysper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractIdleService;
import com.stackframe.sarariman.Authenticator;
import com.stackframe.sarariman.AuthenticatorImpl;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.projects.Project;
import com.stackframe.sarariman.xmpp.Presence;
import com.stackframe.sarariman.xmpp.PresenceType;
import com.stackframe.sarariman.xmpp.Room;
import com.stackframe.sarariman.xmpp.ShowType;
import com.stackframe.sarariman.xmpp.XMPPServer;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.vysper.mina.TCPEndpoint;
import org.apache.vysper.storage.OpenStorageProviderRegistry;
import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authorization.AccountCreationException;
import org.apache.vysper.xmpp.authorization.AccountManagement;
import org.apache.vysper.xmpp.authorization.UserAuthorization;
import org.apache.vysper.xmpp.delivery.StanzaRelay;
import org.apache.vysper.xmpp.delivery.failure.DeliveryException;
import org.apache.vysper.xmpp.delivery.failure.DeliveryFailureStrategy;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.MUCModule;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Conference;
import org.apache.vysper.xmpp.modules.extension.xep0049_privatedata.PrivateDataModule;
import org.apache.vysper.xmpp.modules.extension.xep0054_vcardtemp.VcardTempModule;
import org.apache.vysper.xmpp.modules.extension.xep0060_pubsub.PublishSubscribeModule;
import org.apache.vysper.xmpp.modules.extension.xep0092_software_version.SoftwareVersionModule;
import org.apache.vysper.xmpp.modules.extension.xep0119_xmppping.XmppPingModule;
import org.apache.vysper.xmpp.modules.extension.xep0202_entity_time.EntityTimeModule;
import org.apache.vysper.xmpp.modules.roster.AskSubscriptionType;
import org.apache.vysper.xmpp.modules.roster.Roster;
import org.apache.vysper.xmpp.modules.roster.RosterException;
import org.apache.vysper.xmpp.modules.roster.RosterGroup;
import org.apache.vysper.xmpp.modules.roster.RosterItem;
import org.apache.vysper.xmpp.modules.roster.SubscriptionType;
import org.apache.vysper.xmpp.modules.roster.persistence.RosterManager;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.PresenceStanza;
import org.apache.vysper.xmpp.stanza.PresenceStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
import org.apache.vysper.xmpp.state.presence.LatestPresenceCache;
import org.apache.vysper.xmpp.state.resourcebinding.ResourceRegistry;

/**
 *
 * @author mcculley
 */
public class VysperXMPPServer extends AbstractIdleService implements XMPPServer {

    private final org.apache.vysper.xmpp.server.XMPPServer xmpp;

    private final Directory directory;

    private final File keyStore;

    private final String keyStorePassword;

    private final Executor executor;

    private final String domain;

    private final Conference conference = new Conference("Conference");

    private final DataSource dataSource;

    private final Executor databaseWriteExecutor;

    public VysperXMPPServer(String domain, Directory directory, File keyStore, String keyStorePassword, Executor executor,
                            DataSource dataSource, Executor databaseWriteExecutor) {
        xmpp = new org.apache.vysper.xmpp.server.XMPPServer(domain);
        this.domain = domain;
        this.directory = directory;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.executor = executor;
        this.dataSource = dataSource;
        this.databaseWriteExecutor = databaseWriteExecutor;
    }

    private Entity entity(Employee employee) {
        return new EntityImpl(employee.getUserName(), domain, null);
    }

    private Entity entity(Employee employee, String resource) {
        return new EntityImpl(employee.getUserName(), domain, resource);
    }

    private final AccountManagement accountManagement = new AccountManagement() {
        public void addUser(Entity entity, String string) throws AccountCreationException {
            throw new AccountCreationException("We do not support adding a user via XMPP/IM/Jabber.");
        }

        public void changePassword(Entity entity, String string) throws AccountCreationException {
            throw new AccountCreationException("We do not (yet) support changing a password via XMPP/IM/Jabber.");
        }

        public boolean verifyAccountExists(Entity entity) {
            return employee(entity) != null;
        }

    };

    private final UserAuthorization userAuthAdaptor = new UserAuthorization() {
        public boolean verifyCredentials(Entity entity, String passwordCleartext, Object credentials) {
            return verifyCredentials(entity.getNode(), passwordCleartext, credentials);
        }

        public boolean verifyCredentials(String username, String passwordCleartext, Object credentials) {
            System.err.println("in verifyCredentials with username. username=" + username);
            final Authenticator authenticator = new AuthenticatorImpl(directory);
            return authenticator.checkCredentials(username, passwordCleartext);
        }

    };

    private final RosterManager rosterManager = new RosterManager() {
        private Set<String> groups(Employee employee) {
            ImmutableSet.Builder<String> groupNames = ImmutableSet.<String>builder();
            for (Project project : employee.getRelatedProjects()) {
                groupNames.add(project.getName());
            }

            return groupNames.build();
        }

        private final Set<String> defaultGroups = ImmutableSet.of("staff");

        // FIXME: This is a weird special case. Consider adding some special bit to Project abstraction.
        private final Set<String> groupsToIgnore = ImmutableSet.of("overhead");

        private List<String> commonGroupNames(Employee e1, Employee e2) {
            List<String> groupNames = new ArrayList<String>();
            groupNames.addAll(defaultGroups);
            groupNames.addAll(Sets.intersection(groups(e1), groups(e2)));
            groupNames.removeAll(groupsToIgnore);
            return groupNames;
        }

        private List<RosterGroup> commonGroups(Employee e1, Employee e2) {
            List<RosterGroup> groups = new ArrayList<RosterGroup>();
            for (String groupName : commonGroupNames(e1, e2)) {
                groups.add(new RosterGroup(groupName));
            }

            return groups;
        }

        private RosterItem rosterItem(Employee employee, List<RosterGroup> groups) {
            return new RosterItem(entity(employee), employee.getDisplayName(), SubscriptionType.BOTH,
                                  AskSubscriptionType.ASK_SUBSCRIBED, groups);
        }

        public Roster retrieve(final Entity entity) throws RosterException {
            return new Roster() {
                public Iterator<RosterItem> iterator() {
                    Collection<RosterItem> items = new ArrayList<RosterItem>();
                    Employee employee = employee(entity);
                    Collection<Employee> peers = peers(employee);
                    for (Employee peer : peers) {
                        items.add(rosterItem(peer, commonGroups(employee, peer)));
                    }

                    return Collections.unmodifiableCollection(items).iterator();
                }

                public RosterItem getEntry(Entity peerEntity) {
                    Employee employee = employee(entity);
                    Employee peer = employee(peerEntity);
                    return rosterItem(peer, commonGroups(employee, peer));
                }

            };
        }

        public void addContact(Entity entity, RosterItem ri) throws RosterException {
            throw new RosterException("We don't (yet) support allowing an employee to manipulate the roster.");
        }

        public RosterItem getContact(Entity entity, Entity peerEntity) throws RosterException {
            Employee employee = employee(entity);
            Employee peer = employee(peerEntity);
            return rosterItem(peer, commonGroups(employee, peer));
        }

        public void removeContact(Entity entity, Entity entity1) throws RosterException {
            throw new RosterException("We don't (yet) support allowing an employee to manipulate the roster.");
        }

    };

    @Override
    protected void startUp() throws Exception {
        ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout());
        Logger.getRootLogger().addAppender(consoleAppender);
        StorageProviderRegistry providerRegistry = new OpenStorageProviderRegistry() {
            {
                add(new ArchivedRoomStorageProvider(dataSource, databaseWriteExecutor));
                add(accountManagement);
                add(userAuthAdaptor);
                add(rosterManager);
                add("org.apache.vysper.xmpp.modules.extension.xep0060_pubsub.storageprovider.LeafNodeInMemoryStorageProvider");
                add("org.apache.vysper.xmpp.modules.extension.xep0060_pubsub.storageprovider.CollectionNodeInMemoryStorageProvider");
            }

        };

        xmpp.addEndpoint(new TCPEndpoint());
        xmpp.setStorageProviderRegistry(providerRegistry);
        xmpp.setTLSCertificateInfo(keyStore, keyStorePassword);

        xmpp.start();

        xmpp.addModule(new MUCModule("conference", conference));
        xmpp.addModule(new XmppPingModule());
        xmpp.addModule(new SoftwareVersionModule());
        xmpp.addModule(new EntityTimeModule());
        xmpp.addModule(new VcardTempModule());
        xmpp.addModule(new PrivateDataModule());
        xmpp.addModule(new PublishSubscribeModule());
    }

    @Override
    protected void shutDown() throws Exception {
        xmpp.stop();
    }

    @Override
    protected String serviceName() {
        return "XMPP Server";
    }

    @Override
    protected Executor executor() {
        return executor;
    }

    public Collection<Room> getRooms() {
        Collection<org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Room> rooms = conference.getAllRooms();
        ImmutableList.Builder<Room> b = ImmutableList.<Room>builder();
        for (org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Room room : rooms) {
            final org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Room concreteCopy = room;
            Room r = new Room() {
                public String getName() {
                    return concreteCopy.getName();
                }

            };
            b.add(r);
        }

        return b.build();
    }

    private static PresenceType type(PresenceStanza stanza) {
        if (PresenceStanzaType.isAvailable(stanza.getPresenceType())) {
            return PresenceType.available;
        } else {
            return PresenceType.unavailable;
        }
    }

    private PresenceStanza presenceStanza(Employee employee, Presence p, Entity to) {
        Entity from = entity(employee);
        String lang = null;
        String show = p.getShow().toString();
        String status = p.getStatus();
        PresenceStanzaType type = p.getType() == PresenceType.unavailable ? PresenceStanzaType.UNAVAILABLE : null;
        StanzaBuilder b = StanzaBuilder.createPresenceStanza(from, to, lang, type, show, status);
        return new PresenceStanza(b.build());
    }

    private Employee employee(Entity entity) {
        return employeeFromJID(entity.getBareJID().toString());
    }

    private Employee employeeFromJID(String jid) {
        String bareUserName = jid.substring(0, jid.indexOf('@'));
        return directory.getByUserName().get(bareUserName);
    }

    public Presence getPresence(String username) {
        Employee employee = employeeFromJID(username);
        LatestPresenceCache presenceCache = xmpp.getServerRuntimeContext().getPresenceCache();
        PresenceStanza presence = presenceCache.getForBareJID(entity(employee));
        if (presence == null) {
            return new Presence(PresenceType.unavailable, ShowType.away, null);
        } else {
            try {
                String showString = presence.getShow();
                ShowType show = showString == null ? ShowType.away : ShowType.valueOf(showString);
                Presence p = new Presence(type(presence), show, presence.getStatus(null));
                return p;
            } catch (Exception e) {
                System.err.println("exception getting presence status. e=" + e);
                e.printStackTrace();
                return new Presence(PresenceType.unavailable, ShowType.away, null);
            }
        }
    }

    private Collection<Employee> peers(Employee e) {
        Collection<Employee> result = new ArrayList<Employee>();
        for (Employee employee : directory.getEmployees()) {
            if (!employee.isActive()) {
                continue;
            }

            if (employee == e) {
                continue;
            }

            result.add(employee);
        }

        return result;
    }

    public void setPresence(String username, Presence presence) {
        LatestPresenceCache presenceCache = xmpp.getServerRuntimeContext().getPresenceCache();
        Employee employee = employeeFromJID(username);
        presenceCache.put(entity(employee, "sarariman"), presenceStanza(employee, presence, null));
        for (Employee peer : peers(employee)) {
            Entity to = entity(peer);
            Stanza stanza = presenceStanza(employee, presence, to);
            ServerRuntimeContext src = xmpp.getServerRuntimeContext();
            StanzaRelay relay = src.getStanzaRelay();
            ResourceRegistry resourceRegistry = src.getResourceRegistry();
            List<SessionContext> sessions = resourceRegistry.getSessions(to);
            for (SessionContext session : sessions) {
                for (String resource : resourceRegistry.getResourcesForSession(session)) {
                    Entity e = new EntityImpl(to.getNode(), to.getDomain(), resource);
                    try {
                        relay.relay(e, stanza, new DeliveryFailureStrategy() {
                            public void process(Stanza stanza, List<DeliveryException> list) throws DeliveryException {
                                for (DeliveryException de : list) {
                                    // FIXME: Log this?
                                    System.err.println("de=" + de);
                                    de.printStackTrace();
                                }
                            }

                        });
                    } catch (DeliveryException de) {
                        System.err.println("deliveryException=" + de);
                        de.printStackTrace();
                    }
                }
            }
        }
    }

}
