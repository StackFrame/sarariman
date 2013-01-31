/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class AuditResult {

    private final AuditResultType type;
    private final String message;

    public AuditResult(AuditResultType type, String message) {
        this.type = type;
        this.message = message;
    }

    public AuditResultType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOkay() {
        return type == AuditResultType.okay;
    }

}
