/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class HTMLUtils {

    private HTMLUtils() {
    }

    public static boolean containsHTML(String s) {
        // FIXME: Make this handle other elements.
        return s.contains("<p>") || s.contains("<li>") || s.contains("<br/>");
    }

}
