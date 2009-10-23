/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.math.BigDecimal;

/**
 *
 * @author mcculley
 */
public class CostData {

    private final BigDecimal cost;
    private final String laborCategory;
    private final BigDecimal rate;

    public CostData(BigDecimal cost, String laborCategory, BigDecimal rate) {
        this.cost = cost;
        this.laborCategory = laborCategory;
        this.rate = rate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getLaborCategory() {
        return laborCategory;
    }

    public BigDecimal getRate() {
        return rate;
    }

}
