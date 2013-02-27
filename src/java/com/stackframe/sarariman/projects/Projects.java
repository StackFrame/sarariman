/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface Projects {

    Iterable<Project> getAll();

    Map<? extends Number, Project> getMap();

    Project create(String name, Long customer, Date pop_start, Date pop_end, String contract, String subcontract, BigDecimal funded,
            BigDecimal previouslyBilled, long terms, BigDecimal odc_fee, boolean active, String invoiceText);

}
