/*
 * Copyright (C) 2009-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import static com.stackframe.sql.SQLUtilities.convert;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
public class LDAPDirectory implements Directory {

    private final Sarariman sarariman;

    private final DirContext context;

    private Map<Object, Employee> byNumber;

    private Map<String, Employee> byUserName;

    private Set<Employee> employees;

    public LDAPDirectory(DirContext context, Sarariman sarariman) {
        this.context = context;
        this.sarariman = sarariman;
        load();
    }

    private static String getAttribute(Attributes attributes, String attributeName) throws NamingException {
        Attribute attribute = attributes.get(attributeName);
        if (attribute == null) {
            return null;
        } else {
            return attribute.getAll().next().toString();
        }
    }

    private static Iterable<String> getAttributes(Attributes attributes, String attributeName) throws NamingException {
        Attribute attribute = attributes.get(attributeName);
        ImmutableList.Builder<String> b = ImmutableList.<String>builder();
        if (attribute == null) {
            return null;
        } else {
            NamingEnumeration<?> ne = attribute.getAll();
            while (ne.hasMoreElements()) {
                b.add((String)ne.nextElement());
            }

            return b.build();
        }
    }

    private static Iterable<URL> getURLs(Attributes attributes, String attributeName) throws NamingException {
        Iterable<String> i = getAttributes(attributes, attributeName);
        ImmutableList.Builder<URL> b = ImmutableList.<URL>builder();

        if (i != null) {
            for (String s : i) {
                try {
                    b.add(new URL(s));
                } catch (MalformedURLException mue) {
                    throw new RuntimeException(mue);
                }
            }
        }

        return b.build();
    }
    /*
     FIXME: It would be nice to intercept lookups on the maps and try a reload when a lookup fails.  This would require doing
     something different with the defensive copies.
     */

    /**
     * Load the directory from LDAP.
     */
    private void load() {
        try {
            List<Employee> tmp = new ArrayList<>();
            NamingEnumeration<SearchResult> answer = context.search("ou=People", null,
                                                                    new String[]{"uid", "sn", "givenName", "employeeNumber",
                                                                                 "fulltime", "active", "mail", "birthdate",
                                                                                 "displayName", "hiredate", "jpegPhoto", "mobile",
                                                                                 "url", "title"});
            while (answer.hasMore()) {
                Attributes attributes = answer.next().getAttributes();
                String givenName = attributes.get("givenName").getAll().next().toString();
                String surname = attributes.get("sn").getAll().next().toString();
                String name = surname + ", " + givenName;
                String uid = attributes.get("uid").getAll().next().toString();
                String displayName = attributes.get("displayName").getAll().next().toString();
                String mail = attributes.get("mail").getAll().next().toString();
                boolean fulltime = Boolean.parseBoolean(attributes.get("fulltime").getAll().next().toString());
                boolean active = Boolean.parseBoolean(attributes.get("active").getAll().next().toString());
                String mobile = getAttribute(attributes, "mobile");
                int employeeNumber = Integer.parseInt(attributes.get("employeeNumber").getAll().next().toString());
                LocalDate birthdate = new LocalDate(attributes.get("birthdate").getAll().next().toString());
                LocalDate hiredate = new LocalDate(attributes.get("hiredate").getAll().next().toString());
                Range<java.sql.Date> periodOfService = Range.atLeast(convert(hiredate.toDateMidnight().toDate()));
                Attribute jpegPhotoAttribute = attributes.get("jpegPhoto");
                byte[] photo = jpegPhotoAttribute == null ? null : (byte[])jpegPhotoAttribute.getAll().next();
                Iterable<URL> profileLinks = getURLs(attributes, "url");
                Iterable<String> titles = getAttributes(attributes, "title");
                tmp.add(new StackFrameEmployee(name, givenName, surname, uid, employeeNumber, fulltime, active, mail, birthdate,
                                               displayName, periodOfService, photo, this, sarariman.getDataSource(), sarariman,
                                               mobile, profileLinks, titles));
            }

            Collections.sort(tmp, (Employee e1, Employee e2) -> e1.getFullName().compareTo(e2.getFullName()));

            ImmutableMap.Builder<String, Employee> byUserNameBuilder = new ImmutableMap.Builder<>();
            ImmutableMap.Builder<Object, Employee> byNumberBuilder = new ImmutableMap.Builder<>();
            ImmutableSet.Builder<Employee> employeeBuilder = new ImmutableSet.Builder<>();
            for (Employee employee : tmp) {
                byNumberBuilder.put(employee.getNumber(), employee);

                // We index by employee number as long instead of int so as to be compatible with JSP EL.
                long employeeNumberAsLong = employee.getNumber();
                byNumberBuilder.put(employeeNumberAsLong, employee);

                byNumberBuilder.put(Integer.toString(employee.getNumber()), employee);
                byUserNameBuilder.put(employee.getUserName(), employee);
                employeeBuilder.add(employee);
            }

            byUserName = byUserNameBuilder.build();
            byNumber = byNumberBuilder.build();
            employees = employeeBuilder.build();
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    @Override
    public synchronized Map<String, Employee> getByUserName() {
        return byUserName;
    }

    @Override
    public synchronized Map<Object, Employee> getByNumber() {
        return byNumber;
    }

    @Override
    public synchronized Set<Employee> getEmployees() {
        return employees;
    }

    @Override
    public synchronized void reload() {
        load();
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        String DN = String.format("uid=%s,ou=People,dc=stackframe,dc=com", username);
        try {
            DirContext c = new InitialDirContext((Hashtable<?, ?>)context.getEnvironment().clone());
            c.addToEnvironment(Context.SECURITY_PRINCIPAL, DN);
            c.addToEnvironment(Context.SECURITY_CREDENTIALS, password);

            // This is the validation check, it will throw NamingException if the password is invalid for the username.
            DirContext dirContext = new InitialDirContext((Hashtable<?, ?>)c.getEnvironment().clone());

            dirContext.close();

            Employee employee = getByUserName().get(username);
            return employee.isActive();
        } catch (NamingException e) {
            return false;
        }
    }

}
