/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.logincookies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LoginCookie {

    private final int id;

    private final DataSource dataSource;

    LoginCookie(int id, DataSource dataSource) {
        this.id = id;
        this.dataSource = dataSource;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT username FROM login_cookie WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("username");
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

    public String getUserAgent() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT user_agent FROM login_cookie WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("user_agent");
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

    public void setUserAgent(String userAgent) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE login_cookie SET user_agent = ? WHERE id = ?");
                try {
                    s.setString(1, userAgent);
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

    public String getRemoteAddress() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT remote_address FROM login_cookie WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getString("remote_address");
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

    public void setRemoteAddress(String remoteAddress) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE login_cookie SET remote_address = ? WHERE id = ?");
                try {
                    s.setString(1, remoteAddress);
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

    public Date getCreated() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT created FROM login_cookie WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getDate("created");
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

    public Date getLastUsed() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT last_used FROM login_cookie WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return r.getDate("last_used");
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

    public void setLastUsed(Date lastUsed) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("UPDATE login_cookie SET last_used = ? WHERE id = ?");
                try {
                    s.setTimestamp(1, new Timestamp(lastUsed.getTime()));
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

    public UUID getToken() {
        try {
            Connection connection = dataSource.getConnection();
            try {
                PreparedStatement s = connection.prepareStatement("SELECT token FROM login_cookie WHERE id = ?");
                try {
                    s.setInt(1, id);
                    ResultSet r = s.executeQuery();
                    try {
                        boolean hasRow = r.first();
                        assert hasRow;
                        return UUID.fromString(r.getString("token"));
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

}
