/*
 * Copyright (C) 2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class SAICExtension implements Extension {

    public String getName() {
        return "SAIC";
    }

    public String getTimeReportInclude() {
        return "saic/timereport.jsp";
    }

}
