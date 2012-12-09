/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface Holiday extends Comparable<Holiday> {

    Date getDate();

    String getDescription();

}
