/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import static com.stackframe.sql.SQLUtilities.convert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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

    public LDAPDirectory(DirContext context, Sarariman sarariman) {
        this.context = context;
        this.sarariman = sarariman;
        load();
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
            List<Employee> tmp = new ArrayList<Employee>();
            NamingEnumeration<SearchResult> answer = context.search("ou=People", null,
                                                                    new String[]{"uid", "sn", "givenName", "employeeNumber", "fulltime", "active", "mail", "birthdate", "displayName", "hiredate", "jpegPhoto"});
            while (answer.hasMore()) {
                Attributes attributes = answer.next().getAttributes();
                String name = attributes.get("sn").getAll().next() + ", " + attributes.get("givenName").getAll().next();
                String uid = attributes.get("uid").getAll().next().toString();
                String displayName = attributes.get("displayName").getAll().next().toString();
                String mail = attributes.get("mail").getAll().next().toString();
                boolean fulltime = Boolean.parseBoolean(attributes.get("fulltime").getAll().next().toString());
                boolean active = Boolean.parseBoolean(attributes.get("active").getAll().next().toString());
                int employeeNumber = Integer.parseInt(attributes.get("employeeNumber").getAll().next().toString());
                LocalDate birthdate = new LocalDate(attributes.get("birthdate").getAll().next().toString());
                LocalDate hiredate = new LocalDate(attributes.get("hiredate").getAll().next().toString());
                Range<java.sql.Date> periodOfService = Range.atLeast(convert(hiredate.toDateMidnight().toDate()));
                Attribute jpegPhotoAttribute = attributes.get("jpegPhoto");
                byte[] photo = jpegPhotoAttribute == null ? null : (byte[])jpegPhotoAttribute.getAll().next();
                tmp.add(new StackFrameEmployee(name, uid, employeeNumber, fulltime, active, mail, birthdate, displayName, periodOfService, photo, this, sarariman.getDataSource(), sarariman));
            }

            Collections.sort(tmp, new Comparator<Employee>() {
                public int compare(Employee e1, Employee e2) {
                    return e1.getFullName().compareTo(e2.getFullName());
                }

            });

            ImmutableMap.Builder<String, Employee> byUserNameBuilder = new ImmutableMap.Builder<String, Employee>();
            ImmutableMap.Builder<Object, Employee> byNumberBuilder = new ImmutableMap.Builder<Object, Employee>();
            for (Employee employee : tmp) {
                byNumberBuilder.put(employee.getNumber(), employee);
                byNumberBuilder.put(new Long(employee.getNumber()), employee);
                byNumberBuilder.put(Integer.toString(employee.getNumber()), employee);
                byUserNameBuilder.put(employee.getUserName(), employee);
            }

            byUserName = byUserNameBuilder.build();
            byNumber = byNumberBuilder.build();
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    public synchronized Map<String, Employee> getByUserName() {
        return byUserName;
    }

    public synchronized Map<Object, Employee> getByNumber() {
        return byNumber;
    }

    public synchronized void reload() {
        load();
    }

    public boolean checkCredentials(String username, String password) {
        String DN = String.format("uid=%s,ou=People,dc=stackframe,dc=com", username);
        try {
            Hashtable environment = (Hashtable)context.getEnvironment().clone();
            environment.put(Context.SECURITY_PRINCIPAL, DN);
            environment.put(Context.SECURITY_CREDENTIALS, password);
            DirContext dirContext = new InitialDirContext(environment);
            dirContext.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
