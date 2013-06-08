/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vcard;

/**
 *
 * @author mcculley
 */
public interface vCardRenderer {

    String render(vCardSource source);

    String render(Iterable<vCardSource> sources);

}
