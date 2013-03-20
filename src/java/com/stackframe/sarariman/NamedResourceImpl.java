/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.net.URI;

/**
 *
 * @author mcculley
 */
public class NamedResourceImpl extends AbstractLinkable implements NamedResource {

    private final URI uri;
    private final String name;

    public NamedResourceImpl(URI uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    public URI getURI() {
        return uri;
    }

    public String getName() {
        return name;
    }

}
