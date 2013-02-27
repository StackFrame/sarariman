/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.google.common.base.Function;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.OrganizationHierarchy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectsImpl implements Projects {

    private final DataSource dataSource;
    private final OrganizationHierarchy organizationHierarchy;
    private final Directory directory;

    public ProjectsImpl(DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory) {
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
    }

    public Map<? extends Number, Project> getMap() {
        // FIXME: Need to handle both Long and Integer
        Set<? extends Number> keys = ContiguousSet.create(Range.closed(1L, Long.MAX_VALUE), DiscreteDomain.longs());        
        Function<Number, Project> f = new Function<Number, Project>() {
            public Project apply(Number n) {
                return new ProjectImpl(n.intValue(), dataSource, organizationHierarchy, directory);
            }

        };
        return Maps.asMap(keys, f);
    }

    public Project create(String name, Long customer, Date pop_start, Date pop_end, String contract,
            String subcontract, BigDecimal funded, BigDecimal previouslyBilled, long terms, BigDecimal odc_fee, boolean active,
            String invoiceText) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO projects (name, customer, pop_start, pop_end, contract_number, subcontract_number, funded, previously_billed, terms, odc_fee, active, invoice_text) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                try {
                    ps.setString(1, name);
                    ps.setLong(2, customer);
                    ps.setDate(3, pop_start);
                    ps.setDate(4, pop_end);
                    ps.setString(5, contract);
                    ps.setString(6, subcontract);
                    ps.setBigDecimal(7, funded);
                    ps.setBigDecimal(8, previouslyBilled);
                    ps.setLong(9, terms);
                    ps.setBigDecimal(10, odc_fee);
                    ps.setBoolean(11, active);
                    ps.setString(12, invoiceText);
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    try {
                        rs.next();
                        return new ProjectImpl(rs.getInt(1), dataSource, organizationHierarchy, directory);
                    } finally {
                        rs.close();
                    }
                } finally {
                    ps.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

}
