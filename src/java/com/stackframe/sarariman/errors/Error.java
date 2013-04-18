/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.errors;

import com.stackframe.sarariman.Linkable;
import java.sql.Timestamp;

/**
 *
 * @author mcculley
 */
public interface Error extends Linkable {

    int getId();

    String getStackTrace();

    Timestamp getTimestamp();

}
