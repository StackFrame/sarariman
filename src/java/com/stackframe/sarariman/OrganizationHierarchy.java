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

}
