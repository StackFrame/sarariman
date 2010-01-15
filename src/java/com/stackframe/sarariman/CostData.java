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
    private final LaborCategory laborCategory;
    private final BigDecimal rate;

    public CostData(BigDecimal cost, LaborCategory laborCategory, BigDecimal rate) {
        this.cost = cost;
        this.laborCategory = laborCategory;
        this.rate = rate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public LaborCategory getLaborCategory() {
        return laborCategory;
    }

    public BigDecimal getRate() {
        return rate;
    }

}
