/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.locationlog;

import com.stackframe.sarariman.Directory;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.geolocation.Coordinates;
import com.stackframe.sarariman.geolocation.Position;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LocationLogImpl implements LocationLog {

    // FIXME: Use a single executor for background SQL tasks.
    // FIXME: this needs to be shut down when context stops.
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private final DataSource dataSource;

    private final Directory directory;

    public LocationLogImpl(DataSource dataSource, Directory directory) {
        this.dataSource = dataSource;
        this.directory = directory;
    }

    public void log(final Employee employee, final Coordinates position, final String userAgent, final String remoteAddress) {
        Runnable insertTask = new Runnable() {
            public void run() {
                try {
                    Connection c = dataSource.getConnection();
                    try {
                        PreparedStatement s = c.prepareStatement(
                                "INSERT INTO location_log (employee, latitude, longitude, altitude, accuracy, altitudeAccuracy, heading, speed, user_agent, remote_address) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        try {
                            if (employee == null) {
                                s.setObject(1, null);
                            } else {
                                s.setInt(1, employee.getNumber());
                            }

                            s.setDouble(2, position.latitude);

                            s.setDouble(3, position.longitude);

                            if (position.altitude == null) {
                                s.setObject(4, null);
                            } else {
                                s.setDouble(4, position.altitude);
                            }

                            s.setDouble(5, position.accuracy);

                            if (position.altitudeAccuracy == null) {
                                s.setObject(6, null);
                            } else {
                                s.setDouble(6, position.altitudeAccuracy);
                            }

                            if (position.heading == null) {
                                s.setObject(7, null);
                            } else {
                                s.setDouble(7, position.heading);
                            }

                            if (position.speed == null) {
                                s.setObject(8, null);
                            } else {
                                s.setDouble(8, position.speed);
                            }

                            s.setString(9, userAgent);

                            s.setString(10, remoteAddress);

                            int numRowsInserted = s.executeUpdate();
                            assert numRowsInserted == 1;
                        } finally {
                            s.close();
                        }
                    } finally {
                        c.close();
                    }
                } catch (SQLException e) {
                    // FIXME: Should we log this exception? Does it kill the Executor?
                    throw new RuntimeException(e);
                }
            }

        };
        executor.execute(insertTask);
    }

    public Map<Employee, Position> getLatest() {
        try {
            Connection c = dataSource.getConnection();
            try {
                PreparedStatement s = c.prepareStatement(
                        "SELECT ordered_by_timestamp.* FROM " +
                        "(SELECT * FROM location_log ORDER BY timestamp DESC) AS ordered_by_timestamp " +
                        "GROUP BY employee;");
                try {
                    ResultSet r = s.executeQuery();
                    try {
                        Map map = new HashMap<Employee, Position>();
                        while (r.next()) {
                            int employeeNumber = r.getInt("employee");
                            Employee employee;
                            if (r.wasNull()) {
                                employee = null;
                            } else {
                                employee = directory.getByNumber().get(employeeNumber);
                            }

                            double latitude = r.getDouble("latitude");
                            double longitude = r.getDouble("longitude");
                            Double altitude = r.getDouble("altitude");
                            if (r.wasNull()) {
                                altitude = null;
                            }

                            double accuracy = r.getDouble("accuracy");
                            Double altitudeAccuracy = r.getDouble("altitudeAccuracy");
                            if (r.wasNull()) {
                                altitudeAccuracy = null;
                            }
                            Double heading = r.getDouble("heading");
                            if (r.wasNull()) {
                                heading = null;
                            }
                            Double speed = r.getDouble("speed");
                            if (r.wasNull()) {
                                speed = null;
                            }

                            Coordinates coords = new Coordinates(latitude, longitude, altitude, accuracy, altitudeAccuracy, heading,
                                                                 speed);
                            Timestamp timestamp = r.getTimestamp("timestamp");
                            Position position = new Position(coords, timestamp.getTime());
                            if (employee != null) {
                                map.put(employee, position);
                            }
                        }

                        return map;
                    } finally {
                        r.close();
                    }
                } finally {
                    s.close();
                }
            } finally {
                c.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
