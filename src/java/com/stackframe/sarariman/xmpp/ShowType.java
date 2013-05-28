/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 *
 * @author mcculley
 */
public enum ShowType {

    away, chat, dnd, xa;

    private static final Set<String> names;

    static {
        ImmutableSet.Builder<String> b = ImmutableSet.<String>builder();
        for (ShowType t : values()) {
            b.add(t.name());
        }

        names = b.build();
    }

    public static boolean isValid(String name) {
        return names.contains(name);
    }

}
