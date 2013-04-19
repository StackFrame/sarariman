/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.vacation;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.stackframe.base.Numbers;
import com.stackframe.sarariman.Directory;
import java.util.Map;
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
        Function<Number, VacationEntry> f = new Function<Number, VacationEntry>() {
            public VacationEntry apply(Number n) {
                return get(n.intValue());
            }

        };
        return Maps.asMap(Numbers.positiveIntegers, f);

    }

}
