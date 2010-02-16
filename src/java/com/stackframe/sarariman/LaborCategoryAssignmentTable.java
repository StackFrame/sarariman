/*
 * Copyright (C) 2009 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mcculley
 */
public class LaborCategoryAssignmentTable extends AbstractCollection<LaborCategoryAssignment> {

    private final Sarariman sarariman;

    LaborCategoryAssignmentTable(Sarariman sarariman) {
        this.sarariman = sarariman;
    }

    public Iterator<LaborCategoryAssignment> iterator() {
        final List<LaborCategoryAssignment> rates = new ArrayList<LaborCategoryAssignment>();
        try {
            Connection connection = sarariman.openConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM labor_category_assignments");
            try {
                ResultSet resultSet = ps.executeQuery();
                try {
                    while (resultSet.next()) {
                        int employeeNumber = resultSet.getInt("employee");
                        LaborCategoryAssignment value = new LaborCategoryAssignment(resultSet.getInt("labor_category"), sarariman.getDirectory().getByNumber().get(employeeNumber), resultSet.getDate("pop_start"), resultSet.getDate("pop_end"));
                        rates.add(value);
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                ps.close();
                connection.close();
            }
            final Iterator<LaborCategoryAssignment> iterator = rates.iterator();
            return new Iterator<LaborCategoryAssignment>() {

                public boolean hasNext() {
                    return iterator.hasNext();
                }

                public LaborCategoryAssignment next() {
                    return iterator.next();
                }

                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as numrows FROM labor_category_assignments");
            try {
                ResultSet resultSet = ps.executeQuery();
                try {
                    resultSet.next();
                    return resultSet.getInt("numrows");
                } finally {
                    resultSet.close();
                }
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
