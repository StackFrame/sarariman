/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.invoices;

import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Invoices {

    Invoice get(int id);

    Map<? extends Number, Invoice> getMap();

}
