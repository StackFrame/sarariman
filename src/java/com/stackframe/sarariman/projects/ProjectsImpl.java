/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.stackframe.base.Numbers;
import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.OrganizationHierarchy;
import com.stackframe.sarariman.Sarariman;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
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
public class ProjectsImpl implements Projects {

    private final DataSource dataSource;
    private final OrganizationHierarchy organizationHierarchy;
    private final Directory directory;
    private final Sarariman sarariman;

    public ProjectsImpl(DataSource dataSource, OrganizationHierarchy organizationHierarchy, Directory directory, Sarariman sarariman) {
        this.dataSource = dataSource;
        this.organizationHierarchy = organizationHierarchy;
        this.directory = directory;
        this.sarariman = sarariman;
    }

    public Project get(int id) {
        return new ProjectImpl(id, dataSource, organizationHierarchy, directory, sarariman.getTasks(), this,
                               sarariman.getMountPoint() + "project", sarariman.getClients(), sarariman.getWorkdays(),
                               sarariman.getOutOfOfficeEntries(), sarariman.getLaborProjections(), sarariman.getMountPoint());
    }

    public Map<? extends Number, Project> getMap() {
        Function<Number, Project> f = new Function<Number, Project>() {
            public Project apply(Number n) {
                return get(n.intValue());
            }

        };
        return Maps.asMap(Numbers.positiveIntegers, f);
    }

    public Iterable<Project> getAll() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                Statement s = connection.createStatement();
                try {
                    ResultSet r = s.executeQuery("SELECT id FROM projects");
                    try {
                        Collection<Project> c = new ArrayList<Project>();
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
                        return get(rs.getInt(1));
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
