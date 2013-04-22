/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Linkable;
import com.stackframe.sarariman.PeriodOfPerformance;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface ProjectedExpenses extends Linkable {

    Project getProject();

    Collection<ProjectedExpense> getLabor(PeriodOfPerformance pop);

}
