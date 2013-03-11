/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vacation;

import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Vacations {

    VacationEntry get(int id);

    Map<? extends Number, VacationEntry> getMap();

}
