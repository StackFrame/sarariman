/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.invoices;

import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author mcculley
 */
public interface Invoice {

    int getId();

    Collection<Credit> getCredits();

    BigDecimal getCreditsTotal();

}
