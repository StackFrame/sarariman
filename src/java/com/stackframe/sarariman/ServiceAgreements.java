/*
 * Copyright (C) 2011 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.joda.time.DateMidnight;

/**
 *
 * @author mcculley
 */
public class ServiceAgreements {

    public static Collection<BilledService> getMissingBillings(Sarariman sarariman, int serviceAgreement) throws SQLException {
        ServiceAgreement a = ServiceAgreement.lookup(sarariman, serviceAgreement);
        if (a.getPopStart().getDayOfMonth() != 1) {
            throw new AssertionError("not expecting pop start to be on day other than start of month dom=" + a.getPopStart().getDayOfMonth());
        }

        if (!a.getBillingPeriod().equals("monthly")) {
            throw new AssertionError("not expecting billing period other than monthly");
        }

        DateMidnight now = new DateMidnight();
        DateMidnight endOfThisMonth = new DateMidnight(now.getYear(), now.getMonthOfYear(), 1).plusMonths(1).minusDays(1);
        Set<BilledService> shouldHave = new TreeSet<BilledService>();

        DateMidnight monthStart = a.getPopStart();
        while (true) {
            DateMidnight monthEnd = monthStart.plusMonths(1).minusDays(1);
            BilledService b = new BilledService(-1, a.getID(), monthStart, monthEnd, null);
            shouldHave.add(b);
            monthStart = monthStart.plusMonths(1);
            if (monthStart.isAfter(endOfThisMonth) || monthStart.isAfter(a.getPopEnd())) {
                break;
            }
        }

        List<BilledService> existing = BilledService.lookupByServiceAgreement(sarariman, serviceAgreement);
        shouldHave.removeAll(existing);
        return shouldHave;
    }

}
