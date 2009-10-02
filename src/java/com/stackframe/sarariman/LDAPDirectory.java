package com.stackframe.sarariman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

/**
 *
 * @author mcculley
 */
public class LDAPDirectory implements Directory {

    public static class EmployeeImpl implements Employee {

        private final String fullName;
        private final String userName;
        private final int number;
        private final boolean fulltime;

        public EmployeeImpl(String fullName, String userName, int number, boolean fulltime) {
            this.fullName = fullName;
            this.userName = userName;
            this.number = number;
            this.fulltime = fulltime;
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

        @Override
        public String toString() {
            return "{" + fullName + "," + userName + "," + number + ",fulltime=" + fulltime + "}";
        }

    }
    private final Map<Object, Employee> employees = new HashMap<Object, Employee>();
    private final List<Employee> allEmployees = new ArrayList<Employee>();

    public LDAPDirectory(DirContext context) {
        try {
            NamingEnumeration<SearchResult> answer = context.search("ou=People", new BasicAttributes("active", "TRUE"),
                    new String[]{"uid", "sn", "givenName", "employeeNumber", "fulltime"});
            while (answer.hasMore()) {
                Attributes attributes = answer.next().getAttributes();
                String name = attributes.get("sn").getAll().next() + ", " + attributes.get("givenName").getAll().next();
                String uid = attributes.get("uid").getAll().next().toString();
                boolean fulltime = Boolean.parseBoolean(attributes.get("fulltime").getAll().next().toString());
                int employeeNumber = Integer.parseInt(attributes.get("employeeNumber").getAll().next().toString());
                Employee employee = new EmployeeImpl(name, uid, employeeNumber, fulltime);
                employees.put(employeeNumber, employee);
                employees.put(Integer.toString(employeeNumber), employee);
                employees.put(new Long(employeeNumber), employee);
                employees.put(uid, employee);
                allEmployees.add(employee);
            }

            Collections.sort(allEmployees, new Comparator<Employee>() {

                public int compare(Employee e1, Employee e2) {
                    return e1.getFullName().compareTo(e2.getFullName());
                }

            });
            context.close();
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    public Map<Object, Employee> getEmployeeMap() {
        return employees;
    }

    public Collection<Employee> getEmployees() {
        return allEmployees;
    }

}
