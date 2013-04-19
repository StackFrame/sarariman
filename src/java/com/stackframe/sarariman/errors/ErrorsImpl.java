/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.errors;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.stackframe.base.Numbers;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ErrorsImpl implements Errors {

    private final DataSource dataSource;
    private final String mountPoint;

    public ErrorsImpl(DataSource dataSource, String mountPoint) {
        this.dataSource = dataSource;
        this.mountPoint = mountPoint;
    }

    public Error get(int id) {
        return new ErrorImpl(id, dataSource, mountPoint);
    }

    public Collection<Error> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery("SELECT id FROM error_log ORDER BY timestamp DESC");
                    try {
                        Collection<Error> c = new ArrayList<Error>();
                        while (r.next()) {
                            c.add(get(r.getInt("id")));
                        }

                        return c;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Map<? extends Number, Error> getMap() {
        Function<Number, Error> f = new Function<Number, Error>() {
            public Error apply(Number f) {
                return get(f.intValue());
            }

        };
        return Maps.asMap(Numbers.positiveIntegers, f);
    }

}
