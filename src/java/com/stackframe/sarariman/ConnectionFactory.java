/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;

/**
 *
 * @author mcculley
 */
public interface ConnectionFactory {

    Connection openConnection();

}
