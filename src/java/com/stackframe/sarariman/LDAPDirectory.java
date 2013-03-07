/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Range;
import static com.stackframe.sql.SQLUtilities.convert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
public class LDAPDirectory implements Directory {

    private final Sarariman sarariman;
    private final DirContext context;
    private final Map<Object, Employee> byNumber = new LinkedHashMap<Object, Employee>();
    private final Map<String, Employee> byUserName = new LinkedHashMap<String, Employee>();

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
                    new String[]{"uid", "sn", "givenName", "employeeNumber", "fulltime", "active", "mail", "birthdate", "displayName", "hiredate"});
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
                tmp.add(new StackFrameEmployee(name, uid, employeeNumber, fulltime, active, mail, birthdate, displayName, periodOfService, this, sarariman.getDataSource(), sarariman));
            }

            Collections.sort(tmp, new Comparator<Employee>() {
                public int compare(Employee e1, Employee e2) {
                    return e1.getFullName().compareTo(e2.getFullName());
                }

            });

            for (Employee employee : tmp) {
                byNumber.put(employee.getNumber(), employee);
                byNumber.put(new Long(employee.getNumber()), employee);
                byNumber.put(Integer.toString(employee.getNumber()), employee);
                byUserName.put(employee.getUserName(), employee);
            }
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    private static <K, V> Map<K, V> copy(Map<K, V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<K, V>(map));
    }

    public synchronized Map<String, Employee> getByUserName() {
        return copy(byUserName);
    }

    public synchronized Map<Object, Employee> getByNumber() {
        return copy(byNumber);
    }

    public synchronized void reload() {
        byNumber.clear();
        byUserName.clear();
        load();
    }

}
