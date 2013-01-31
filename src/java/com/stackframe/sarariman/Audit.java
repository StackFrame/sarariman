/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface Audit {

    String getDisplayName();

    Collection<AuditResult> getResults();

}
