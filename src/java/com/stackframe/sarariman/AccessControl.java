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
