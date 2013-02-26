/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.clients.Client;
import com.stackframe.sarariman.clients.ClientImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class ProjectImpl implements Project {

    private final int id;
    private final DataSource dataSource;

    public ProjectImpl(int id, DataSource dataSource) {
        this.id = id;
        this.dataSource = dataSource;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT name FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("name");
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

    public void setName(String name) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET name = ? WHERE id = ?");
                try {
                    s.setString(1, name);
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

    public boolean isManager(Employee employee) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM project_managers WHERE employee=? AND project=?");
                try {
                    ps.setInt(1, employee.getNumber());
                    ps.setLong(2, id);
                    ResultSet rs = ps.executeQuery();
                    try {
                        return rs.first();
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

    public boolean isCostManager(Employee employee) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM project_cost_managers WHERE employee=? AND project=?");
                try {
                    ps.setInt(1, employee.getNumber());
                    ps.setLong(2, id);
                    ResultSet rs = ps.executeQuery();
                    try {
                        return rs.first();
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

    public Client getClient() {

        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT customer FROM projects WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        int client_id = r.getInt("customer");
                        if (client_id == 0) {
                            return null;
                        } else {
                            return new ClientImpl(client_id, dataSource);
                        }
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

    public void setClient(Client client) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE projects SET customer = ? WHERE id = ?");
                try {
                    s.setInt(1, client.getId());
                    s.setInt(2, id);
                    int numRows = s.executeUpdate();
                    assert numRows == 1;
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

}
