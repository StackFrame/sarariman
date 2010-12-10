/*
 * Copyright (C) 2009-2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import org.joda.time.DateMidnight;

/**
 *
 * @author mcculley
 */
public class LDAPDirectory implements Directory {

    private final Sarariman sarariman;
    private final DirContext context;
    private final Map<Object, Employee> byNumber = new LinkedHashMap<Object, Employee>();
    private final Map<String, Employee> byUserName = new LinkedHashMap<String, Employee>();

    public class EmployeeImpl implements Employee {

        private final String fullName;
        private final String userName;
        private final int number;
        private final boolean fulltime;
        private final boolean active;
        private final String email;
        private final DateMidnight birthdate;

        public EmployeeImpl(String fullName, String userName, int number, boolean fulltime, boolean active, String email, DateMidnight birthdate) {
            this.fullName = fullName;
            this.userName = userName;
            this.number = number;
            this.fulltime = fulltime;
            this.active = active;
            this.email = email;
            this.birthdate = birthdate;
        }

        public String getFullName() {
            return fullName;
        }

        public String getUserName() {
            return userName;
        }

        public int getNumber() {
            return number;
        }

        public boolean isFulltime() {
            return fulltime;
        }

        public boolean isActive() {
            return active;
        }

        public InternetAddress getEmail() {
            try {
                return new InternetAddress(email, true);
            } catch (AddressException ae) {
                throw new RuntimeException("could not construct an email address", ae);
            }
        }

        public boolean isAdministrator() {
            return sarariman.getAdministrators().contains(this);
        }

        public boolean isApprover() {
            return sarariman.getApprovers().contains(this);
        }

        public boolean isInvoiceManager() {
            return sarariman.getInvoiceManagers().contains(this);
        }

        public DateMidnight getBirthdate() {
            return birthdate;
        }

        @Override
        public String toString() {
            return "{" + fullName + "," + userName + "," + number + ",fulltime=" + fulltime + ",email=" + email + "}";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final EmployeeImpl other = (EmployeeImpl)obj;
            if (this.number != other.number) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + this.number;
            return hash;
        }

    }

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
                    new String[]{"uid", "sn", "givenName", "employeeNumber", "fulltime", "active", "mail", "birthdate"});
            while (answer.hasMore()) {
                Attributes attributes = answer.next().getAttributes();
                String name = attributes.get("sn").getAll().next() + ", " + attributes.get("givenName").getAll().next();
                String uid = attributes.get("uid").getAll().next().toString();
                String mail = attributes.get("mail").getAll().next().toString();
                boolean fulltime = Boolean.parseBoolean(attributes.get("fulltime").getAll().next().toString());
                boolean active = Boolean.parseBoolean(attributes.get("active").getAll().next().toString());
                int employeeNumber = Integer.parseInt(attributes.get("employeeNumber").getAll().next().toString());
                DateMidnight birthdate = new DateMidnight(attributes.get("birthdate").getAll().next().toString());
                tmp.add(new EmployeeImpl(name, uid, employeeNumber, fulltime, active, mail, birthdate));
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
