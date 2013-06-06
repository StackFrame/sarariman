/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.net.URL;

/**
 *
 * @author mcculley
 */
public class PrettyURL {

    private final URL url;

    public PrettyURL(URL url) {
        this.url = url;
    }

    public String getIconClass() {
        String host = url.getHost();
        if (host.equals("www.linkedin.com")) {
            return "icon-linkedin";
        } else if (host.equals("twitter.com")) {
            return "icon-twitter";
        } else if (host.equals("github.com")) {
            return "icon-github";
        } else if (host.equals("www.facebook.com")) {
            return "icon-facebook";
        } else {
            return "icon-link";
        }
    }

    public String getAnchorText() {
        String host = url.getHost();
        if (host.equals("twitter.com")) {
            String u = url.toExternalForm();
            String username = u.substring(u.lastIndexOf('/') + 1);
            return "@" + username;
        } else {
            return url.toExternalForm();
        }
    }

    public URL getURL() {
        return url;
    }

}
