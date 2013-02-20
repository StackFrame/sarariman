/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Range;

/**
 *
 * @author mcculley
 */
public class Utilities {

    private Utilities() {
        /* inhibit construction of utility class */
    }

    /**
     * A Predicate that matches for active employees.
     */
    public static final Predicate<Employee> active = new Predicate<Employee>() {
        public boolean apply(Employee e) {
            return e.isActive();
        }

    };
    /**
     * A Predicate that matches for full time employees.
     */
    public static final Predicate<Employee> fulltime = new Predicate<Employee>() {
        public boolean apply(Employee e) {
            return e.isFulltime();
        }

    };
    /**
     * A Predicate that matches for active full time employees.
     */
    public static final Predicate<Employee> activeFulltime = Predicates.and(active, fulltime);

    /**
     * Determine if a value is contained in any supplied Ranges.
     *
     * @param <C> the class that Range is constrained to
     * @param ranges the ranges to check
     * @param value the value to check for
     * @return true if value is contained in any of the ranges
     */
    public static <C extends Comparable> boolean contains(Iterable<Range<C>> ranges, C value) {
        for (Range<C> r : ranges) {
            if (r.contains(value)) {
                return true;
            }
        }

        return false;
    }

}
