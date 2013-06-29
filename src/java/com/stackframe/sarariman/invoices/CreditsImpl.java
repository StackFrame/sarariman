/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.invoices;

import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class CreditsImpl implements Credits {

    private final DataSource dataSource;

    public CreditsImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Credit get(int id) {
        return new CreditImpl(id, dataSource);
    }

}
