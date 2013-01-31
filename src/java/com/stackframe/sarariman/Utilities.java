/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Predicate;

/**
 *
 * @author mcculley
 */
public class Utilities {

    private Utilities() {
        /* inhibit construction of utility class */
    }

    /**
     * A Predicate that matches for active full time employees.
     */
    public static final Predicate<Employee> activeFulltime = new Predicate<Employee>() {
        public boolean apply(Employee e) {
            return e.isActive() && e.isFulltime();
        }

    };
}
