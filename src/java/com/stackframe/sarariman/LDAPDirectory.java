/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
    private final Function<Integer, Employee> employeeIDToEmployee = new Function<Integer, Employee>() {
        public Employee apply(Integer f) {
            return byNumber.get(f);
        }

    };

    public class EmployeeImpl implements Employee {

        private final String fullName;
        private final String userName;
        private final int number;
        private final boolean fulltime;
        private final boolean active;
        private final String email;
        private final LocalDate birthdate;
        private final String displayName;

        public EmployeeImpl(String fullName, String userName, int number, boolean fulltime, boolean active, String email, LocalDate birthdate, String displayName) {
            this.fullName = fullName;
            this.userName = userName;
            this.number = number;
            this.fulltime = fulltime;
            this.active = active;
            this.email = email;
            this.birthdate = birthdate;
            this.displayName = displayName;
        }

        public String getFullName() {
            return fullName;
        }

        public String getUserName() {
            return userName;
        }

        public String getDisplayName() {
            return displayName;
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
            System.err.println("entering isAdministrator");
            Connection connection = sarariman.openConnection();
            try {
                try {
                    PreparedStatement s = connection.prepareStatement("SELECT administrator FROM employee WHERE id = ?");
                    try {
                        s.setInt(1, number);
                        ResultSet rs = s.executeQuery();
                        try {
                            return rs.first() && rs.getBoolean("administrator");
                        } finally {
                            rs.close();
                        }
                    } finally {
                        s.close();
                    }
                } finally {
                    connection.close();
                }
            } catch (SQLException se) {
                throw new RuntimeException(se);
            }
        }

        public boolean isApprover() {
            return sarariman.getApprovers().contains(this);
        }

        public boolean isInvoiceManager() {
            return sarariman.getInvoiceManagers().contains(this);
        }

        public LocalDate getBirthdate() {
            return birthdate;
        }

        public int getAge() {
            return DateUtils.yearsBetween(birthdate.toDateMidnight(), new Date());
        }

        public SortedSet<Employee> getReports() {
            Collection<Integer> reportIDs = sarariman.getOrganizationHierarchy().getReports(number);
            Collection<Employee> reports = Collections2.transform(reportIDs, employeeIDToEmployee);
            Comparator<Employee> fullNameComparator = new Comparator<Employee>() {
                public int compare(Employee t, Employee t1) {
                    return t.getFullName().compareTo(t1.getFullName());
                }

            };
            return ImmutableSortedSet.copyOf(fullNameComparator, reports);
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
                    new String[]{"uid", "sn", "givenName", "employeeNumber", "fulltime", "active", "mail", "birthdate", "displayName"});
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
                tmp.add(new EmployeeImpl(name, uid, employeeNumber, fulltime, active, mail, birthdate, displayName));
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
