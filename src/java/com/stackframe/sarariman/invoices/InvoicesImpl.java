/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.invoices;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.stackframe.base.Numbers;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class InvoicesImpl implements Invoices {

    private final DataSource dataSource;

    private final Credits credits;

    public InvoicesImpl(DataSource dataSource, Credits credits) {
        this.dataSource = dataSource;
        this.credits = credits;
    }

    public Invoice get(int id) {
        return new InvoiceImpl(id, dataSource, credits);
    }

    public Map<? extends Number, Invoice> getMap() {
        System.err.println("in invoicesImpl::getMap");
        Function<Number, Invoice> f = new Function<Number, Invoice>() {
            public Invoice apply(Number n) {
                System.err.println("in invoicesImpl::getMap::apply n=" + n);
                return get(n.intValue());
            }

        };
        return Maps.asMap(Numbers.positiveIntegers, f);
    }

}
