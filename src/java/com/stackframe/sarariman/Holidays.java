/*
 * Copyright (C) 2012 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mcculley
 */
public interface Holidays {

    List<Holiday> getAll() throws SQLException;

    boolean isHoliday(Date date) throws SQLException;

}
