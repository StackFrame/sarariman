/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author mcculley
 */
public abstract class AbstractLinkable implements Linkable, Identifiable {

    @Override
    public URL getURL() {
        try {
            return getURI().toURL();
        } catch (MalformedURLException mue) {
            throw new AssertionError(mue);
        }
    }

}
