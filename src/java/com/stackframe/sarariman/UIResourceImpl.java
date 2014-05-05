/*
 * Copyright (C) 2013-2104 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.net.URI;

/**
 *
 * @author mcculley
 */
public class UIResourceImpl extends NamedResourceImpl implements UIResource {

    private final String iconName;

    public UIResourceImpl(URI uri, String name, String iconName) {
        super(uri, name);
        this.iconName = iconName;
    }

    public UIResourceImpl(String uri, String name, String iconName) {
        this(URI.create(uri), name, iconName);
    }

    public UIResourceImpl(URI uri, String name) {
        this(uri, name, null);
    }

    public UIResourceImpl(String uri, String name) {
        this(URI.create(uri), name);
    }

    @Override
    public String getIconName() {
        return iconName;
    }

}
