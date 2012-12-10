/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import org.joda.time.LocalDate;

/**
 *
 * @author mcculley
 */
public interface Holiday extends Comparable<Holiday> {

    LocalDate getDate();

    String getDescription();

}
