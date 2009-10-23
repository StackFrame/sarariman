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
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author mcculley
 */
public class LaborCategoryTable extends AbstractCollection<LaborCategory> {

    private final Sarariman sarariman;

    LaborCategoryTable(Sarariman sarariman) {
        this.sarariman = sarariman;
    }

    private Collection<LaborCategory> getLaborCategories() throws SQLException {
        Connection connection = sarariman.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM labor_categories");
        try {
            ResultSet resultSet = ps.executeQuery();
            try {
                Collection<LaborCategory> result = new ArrayList<LaborCategory>();
                while (resultSet.next()) {
                    result.add(new LaborCategory(resultSet.getLong("id"), resultSet.getLong("project"),
                            resultSet.getBigDecimal("rate"), resultSet.getDate("pop_start"), resultSet.getDate("pop_end"),
                            resultSet.getString("name")));
                }
                return result;
            } finally {
                resultSet.close();
            }
        } finally {
            ps.close();
        }
    }

    public Iterator<LaborCategory> iterator() {
        try {
            final Iterator<LaborCategory> administrators = getLaborCategories().iterator();
            return new Iterator<LaborCategory>() {

                LaborCategory current;

                public boolean hasNext() {
                    return administrators.hasNext();
                }

                public LaborCategory next() {
                    current = administrators.next();
                    return current;
                }

                public void remove() {
                    LaborCategoryTable.this.remove(current);
                }

            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        Connection connection = sarariman.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as numrows FROM labor_categories");
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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean add(LaborCategory laborCategory) {
        try {
            PreparedStatement ps = sarariman.getConnection().prepareStatement("INSERT INTO labor_categories (project) VALUES(?)");
            ps.setLong(1, laborCategory.getProject());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof LaborCategory)) {
            return false;
        }

        LaborCategory laborCategory = (LaborCategory)o;
        try {
            PreparedStatement ps = sarariman.getConnection().prepareStatement("DELETE FROM labor_categories WHERE id=?");
            ps.setLong(1, laborCategory.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
