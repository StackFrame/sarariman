/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vcard;

/**
 *
 * @author mcculley
 */
public class vCardRenderers {

    public static vCardRenderer getvCardRenderer(String version) {
        if (version.equals("3.0")) {
            return new vCard3Renderer();
        } else {
            throw new IllegalArgumentException("unsupport version " + version);
        }
    }

    public static xCardRenderer getxCardRenderer(String version) {
        if (version.equals("4.0")) {
            return new xCard4Renderer();
        } else {
            throw new IllegalArgumentException("unsupport version " + version);
        }
    }

}
