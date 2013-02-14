/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Collections2;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public class DirectRateAudit implements Audit {

    private final Directory directory;

    public DirectRateAudit(Directory directory) {
        this.directory = directory;
    }

    public String getDisplayName() {
        return "Direct Rate";
    }

    public Collection<AuditResult> getResults() {
        Collection<AuditResult> c = new ArrayList<AuditResult>();
        Collection<Employee> activeEmployees = Collections2.filter(directory.getByUserName().values(), Utilities.active);
        for (Employee e : activeEmployees) {
            BigDecimal directRate = e.getDirectRate();
            if (directRate == null) {
                c.add(new AuditResult(AuditResultType.error, String.format("%s (%d) has no current direct rate", e.getDisplayName(), e.getNumber())));
            }
        }

        return c;
    }

}
