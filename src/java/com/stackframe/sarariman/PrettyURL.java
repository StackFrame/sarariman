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
public class PrettyURL implements Comparable<PrettyURL> {

    private final URL url;

    private static final String defaultIconClass = "icon-link";

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
            return defaultIconClass;
        }
    }

    public String getAnchorText() {
        String host = url.getHost();
        if (host.equals("twitter.com")) {
            String u = url.toExternalForm();
            String username = u.substring(u.lastIndexOf('/') + 1);
            return "@" + username;
        } else if (host.equals("github.com")) {
            String u = url.toExternalForm();
            return u.substring(u.lastIndexOf('/') + 1);
        } else {
            return url.toExternalForm();
        }
    }

    public URL getURL() {
        return url;
    }

    private int rank() {
        if (!getAnchorText().equals(url.toExternalForm())) {
            return 1;
        } else if (!getIconClass().equals(defaultIconClass)) {
            return 2;
        } else {
            return 3;
        }
    }

    public int compareTo(PrettyURL other) {
        int rank = this.rank();
        int rankOther = other.rank();
        int compared = rank - rankOther;
        if (compared != 0) {
            return compared;
        } else {
            return url.getHost().compareTo(other.url.getHost());
        }
    }

}
