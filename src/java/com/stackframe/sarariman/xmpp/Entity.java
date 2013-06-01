/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.xmpp;

/**
 *
 * @author mcculley
 */
public class Entity {

    private final String node, domain, resource;

    public Entity(String node, String domain, String resource) {
        this.node = node;
        this.domain = domain;
        this.resource = resource;
    }

    public String getNode() {
        return node;
    }

    public String getDomain() {
        return domain;
    }

    public String getResource() {
        return resource;
    }

    public Entity getBareJID() {
        return new Entity(node, domain, null);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (node != null) {
            buf.append(node);
            buf.append('@');
        }

        buf.append(domain);
        if (resource != null) {
            buf.append('/');
            buf.append(resource);
        }

        return buf.toString();
    }

}
