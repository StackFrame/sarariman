/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Date;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface OrganizationHierarchy {

    /**
     * Get a list of managers of an employee on a given date.
     *
     * @param employee the ID of the employee to find managers of
     * @param date the date
     * @return a list of managers of employee on date
     */
    Collection<Integer> getManagers(int employee, Date date);

    /**
     * Get a list of managers of an employee at this time.
     *
     * @param employee the ID of the employee to find managers of
     * @return a list of managers of employee
     */
    Collection<Integer> getManagers(int employee);

    /**
     * Get a list of managers all the way up the chain of an employee on a given date.
     *
     * @param employee the ID of the employee to find managers of
     * @param date the date
     * @return a list of managers of employee on date
     */
    Collection<Integer> getChainsOfCommand(int employee, Date date);

    /**
     * Get a list of managers all the way up the chain of an employee at this time.
     *
     * @param employee the ID of the employee to find managers of
     * @return a list of managers of employee on date
     */
    Collection<Integer> getChainsOfCommand(int employee);

    /**
     * Get a list of the direct reports of an employee on a given date.
     *
     * @param employee the ID of the employee to find direct reports of
     * @param date the date
     * @return a list of direct reports
     */
    Collection<Integer> getDirectReports(int employee, Date date);

    /**
     * Get a list of the direct reports of an employee at this time.
     *
     * @param employee the ID of the employee to find direct reports of
     * @return a list of direct reports
     */
    Collection<Integer> getDirectReports(int employee);

    interface Node {

        int id();

        Collection<Node> directReports();

    }

    /**
     * Builds an org chart for a given date.
     *
     * @param the date
     * @return a tree of Node objects representing the org chart
     */
    Collection<Node> getOrgChart(Date date);

    /**
     * Builds an org chart for this time.
     *
     * @return a tree of Node objects representing the org chart
     */
    Collection<Node> getOrgChart();

}
