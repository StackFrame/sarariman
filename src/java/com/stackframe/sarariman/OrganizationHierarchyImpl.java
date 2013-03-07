/*
 * Copyright (C) 2012-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import static com.stackframe.sql.SQLUtilities.convert;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class OrganizationHierarchyImpl implements OrganizationHierarchy {

    private final DataSource dataSource;
    private final Directory directory;

    public OrganizationHierarchyImpl(DataSource dataSource, Directory directory) {
        this.dataSource = dataSource;
        this.directory = directory;
    }

    public Collection<Integer> getManagers(int employee, Date date) {
        Collection<Integer> managers = new ArrayList<Integer>();
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT manager FROM organization_hierarchy WHERE employee=? AND ((begin <= ? AND end >= ?) OR (begin <= ? AND end IS NULL));");
                try {
                    ps.setInt(1, employee);
                    ps.setDate(2, date);
                    ps.setDate(3, date);
                    ps.setDate(4, date);
                    ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            int managerNumber = rs.getInt("manager");
                            Employee manager = directory.getByNumber().get(managerNumber);
                            if (manager.isActive()) {
                                managers.add(managerNumber);
                            }
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return managers;
    }

    public Collection<Integer> getManagers(int employee) {
        return getManagers(employee, now());
    }

    public Collection<Integer> getChainsOfCommand(int employee, Date date) {
        Collection<Integer> list = new HashSet<Integer>();
        Collection<Integer> toAdd = getManagers(employee, date);
        for (int e : toAdd) {
            list.add(e);
            list.addAll(getChainsOfCommand(e, date));
        }

        return list;
    }

    public Collection<Integer> getChainsOfCommand(int employee) {
        return getChainsOfCommand(employee, now());
    }

    public Collection<Integer> getDirectReports(int employee, Date date, boolean includeInactive) {
        Collection<Integer> managers = new ArrayList<Integer>();
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT employee FROM organization_hierarchy WHERE manager=? AND ((begin <= ? AND end >= ?) OR (begin <= ? AND end IS NULL));");
                try {
                    ps.setInt(1, employee);
                    ps.setDate(2, date);
                    ps.setDate(3, date);
                    ps.setDate(4, date);
                    ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            int employeeNumber = rs.getInt("employee");
                            Employee e = directory.getByNumber().get(employeeNumber);
                            if (includeInactive || e.isActive()) {
                                managers.add(employeeNumber);
                            }
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return managers;
    }

    public Collection<Integer> getDirectReports(int employee, Date date) {
        return getDirectReports(employee, date, false);
    }

    public Collection<Integer> getDirectReports(int employee, boolean includeInactive) {
        return getDirectReports(employee, now(), includeInactive);
    }

    public Collection<Integer> getDirectReports(int employee) {
        return getDirectReports(employee, now(), false);
    }

    public Collection<Integer> getReports(int employee, Date date, boolean includeInactive) {
        Collection<Integer> result = new HashSet<Integer>();
        Collection<Integer> toAdd = getDirectReports(employee, date, includeInactive);
        for (int e : toAdd) {
            result.add(e);
            result.addAll(getReports(e, date, includeInactive));
        }

        return result;
    }

    public Collection<Integer> getReports(int employee, Date date) {
        return getReports(employee, date, false);
    }

    public Collection<Integer> getReports(int employee, boolean includeInactive) {
        return getReports(employee, now(), includeInactive);
    }

    public Collection<Integer> getReports(int employee) {
        return getReports(employee, now(), false);
    }

    public static class EmployeeNode implements Node {

        private final int id;
        private final Collection<Node> directReports = new ArrayList<Node>();

        public EmployeeNode(int id) {
            this.id = id;
        }

        public Collection<Node> directReports() {
            return directReports;
        }

        public int id() {
            return id;
        }

        @Override
        public String toString() {
            return "EmployeeNode{" + "id=" + id + ", directReports=" + directReports + '}';
        }

    }

    private Collection<EmployeeNode> getEmployees(Date date) {
        Collection<EmployeeNode> employees = new ArrayList<EmployeeNode>();
        Set<Integer> ids = new HashSet<Integer>();
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT employee, manager FROM organization_hierarchy WHERE ((begin <= ? AND end >= ?) OR (begin <= ? AND end IS NULL));");
                try {
                    ps.setDate(1, date);
                    ps.setDate(2, date);
                    ps.setDate(3, date);
                    ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            ids.add(rs.getInt("employee"));
                            ids.add(rs.getInt("manager"));
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (int i : ids) {
            employees.add(new EmployeeNode(i));
        }

        return employees;
    }

    public Collection<Node> getOrgChart(Date date) {
        Collection<EmployeeNode> employees = getEmployees(date);
        Collection<Node> bosses = new ArrayList<Node>();
        Map<Integer, EmployeeNode> byNum = new HashMap<Integer, EmployeeNode>();
        for (EmployeeNode employee : employees) {
            byNum.put(employee.id, employee);
            Collection<Integer> managers = getManagers(employee.id, date);
            if (managers.isEmpty()) {
                bosses.add(employee);
            }
        }

        for (EmployeeNode employee : employees) {
            Collection<Integer> directReports = getDirectReports(employee.id, date);
            for (int report : directReports) {
                employee.directReports.add(byNum.get(report));
            }
        }

        return bosses;
    }

    public Collection<Node> getOrgChart() {
        return getOrgChart(now());
    }

    public Collection<Integer> getBosses(Date date) {
        Collection<EmployeeNode> employees = getEmployees(date);
        Collection<Integer> bosses = new ArrayList<Integer>();
        for (EmployeeNode employee : employees) {
            Collection<Integer> managers = getManagers(employee.id, date);
            if (managers.isEmpty()) {
                bosses.add(employee.id);
            }
        }

        return bosses;
    }

    public Collection<Integer> getBosses() {
        return getBosses(now());
    }

    private static Date now() {
        return convert(new java.util.Date());
    }

}
