/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;

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
    public static final Predicate<Employee> active = Employee::isActive;

    /**
     * A Predicate that matches for full time employees.
     */
    public static final Predicate<Employee> fulltime = Employee::isFulltime;

    /**
     * A Predicate that matches for active full time employees.
     */
    public static final Predicate<Employee> activeFulltime = Predicates.and(active, fulltime);

    public static <K, V extends Object> Set<K> allKeys(Iterable<Map<K, V>> maps) {
        ImmutableSet.Builder<K> builder = ImmutableSet.<K>builder();
        for (Map<K, V> map : maps) {
            builder.addAll(map.keySet());
        }

        return builder.build();
    }

}
