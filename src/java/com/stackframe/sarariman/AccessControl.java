/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class AccessControl {

    private AccessControl() {
    }

    public static boolean isAdministrator(Employee employee) {
        return employee.getUserName().equals("mcculley");
    }

}
