/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import static com.google.common.base.Preconditions.*;
import java.net.URL;

/**
 *
 * @author mcculley
 */
public class AuditResult {

    private final AuditResultType type;
    private final String message;
    private final URL url;

    public AuditResult(AuditResultType type, String message, URL url) {
        this.type = checkNotNull(type);
        this.message = checkNotNull(message);
        this.url = checkNotNull(url);
    }

    public AuditResultType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public URL getURL() {
        return url;
    }

}
