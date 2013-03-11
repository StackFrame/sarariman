/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vacation;

import com.google.common.base.Function;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.stackframe.sarariman.Directory;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class VacationsImpl implements Vacations {

    private final DataSource dataSource;
    private final Directory directory;

    public VacationsImpl(DataSource dataSource, Directory directory) {
        this.dataSource = dataSource;
        this.directory = directory;
    }

    public VacationEntry get(int id) {
        return new VacationEntryImpl(id, dataSource, directory);
    }

    public Map<? extends Number, VacationEntry> getMap() {
        Set<? extends Number> longKeys = ContiguousSet.create(Range.greaterThan(0L), DiscreteDomain.longs());
        Set<? extends Number> intKeys = ContiguousSet.create(Range.greaterThan(0), DiscreteDomain.integers());
        Set<? extends Number> keys = Sets.union(longKeys, intKeys);
        Function<Number, VacationEntry> f = new Function<Number, VacationEntry>() {
            public VacationEntry apply(Number n) {
                return get(n.intValue());
            }

        };
        return Maps.asMap(keys, f);

    }

}
