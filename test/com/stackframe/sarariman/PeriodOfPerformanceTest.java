/*
 * Copyright (C) 2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableList;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcculley
 */
public class PeriodOfPerformanceTest {

    public PeriodOfPerformanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getEnd method, of class PeriodOfPerformance.
     */
    @Test
    public void testGetEnd() throws ParseException {
        PeriodOfPerformance instance = new PeriodOfPerformance("2014-05-01", "2014-05-02");
        Date expResult = Week.ISO8601DateFormat().parse("2014-05-02");
        Date result = instance.getEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStart method, of class PeriodOfPerformance.
     */
    @Test
    public void testGetStart() throws ParseException {
        PeriodOfPerformance instance = new PeriodOfPerformance("2014-05-01", "2014-05-02");
        Date expResult = Week.ISO8601DateFormat().parse("2014-05-01");
        Date result = instance.getStart();
        assertEquals(expResult, result);
    }

    /**
     * Test of intersection method, of class PeriodOfPerformance.
     */
    @Test
    public void testIntersection() throws ParseException {
        PeriodOfPerformance pop = new PeriodOfPerformance("2014-05-01", "2014-05-02");
        PeriodOfPerformance instance = new PeriodOfPerformance("2014-05-02", "2014-05-03");
        PeriodOfPerformance expResult = new PeriodOfPerformance("2014-05-02", "2014-05-02");
        PeriodOfPerformance result = instance.intersection(pop);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDays method, of class PeriodOfPerformance.
     */
    @Test
    public void testGetDays() throws ParseException {
        PeriodOfPerformance instance = new PeriodOfPerformance("2014-05-02", "2014-05-03");
        Collection<Date> expResult = ImmutableList.<Date>of(Week.ISO8601DateFormat().parse("2014-05-02"),Week.ISO8601DateFormat().parse("2014-05-03"));
        Collection<Date> result = instance.getDays();
        assertEquals(expResult, result);
    }

}
