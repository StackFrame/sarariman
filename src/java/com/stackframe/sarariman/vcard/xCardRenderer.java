/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vcard;

import org.w3c.dom.Document;

/**
 * A renderer for vCards in the xCard format defined in RFC6351 (http://tools.ietf.org/html/rfc6351)
 *
 * @author mcculley
 */
public interface xCardRenderer {

    Document render(vCardSource source);

    Document render(Iterable<vCardSource> sources);

}
