/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class LogLocation extends HttpServlet {

    private DataSource dataSource;
    // FIXME: Use a single executor for background SQL tasks.
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Override
    public void init() throws ServletException {
        super.init();
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        dataSource = sarariman.getDataSource();
    }

    private static Double getDouble(HttpServletRequest request, String name) {
        String s = request.getParameter("name");
        if (s == null || s.equals("null")) {
            return null;
        } else {
            return Double.parseDouble(s);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Per the specification (http://www.w3.org/TR/geolocation-API/), altitude, altitudeAccuracy, heading, and speed may be
        // null.
        final double latitude = Double.parseDouble(request.getParameter("latitude"));
        final double longitude = Double.parseDouble(request.getParameter("longitude"));
        final Double altitude = getDouble(request, "altitude");
        final double accuracy = Double.parseDouble(request.getParameter("accuracy"));
        final Double altitudeAccuracy = getDouble(request, "altitudeAccuracy");
        final Double heading = getDouble(request, "heading");
        final Double speed = getDouble(request, "speed");

        final String userAgent = request.getHeader("User-Agent");
        final String remoteAddress = request.getRemoteAddr();
        final Employee employee = (Employee)request.getAttribute("user");

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

                            s.setDouble(2, latitude);

                            s.setDouble(3, longitude);

                            if (altitude == null) {
                                s.setObject(4, null);
                            } else {
                                s.setDouble(4, altitude);
                            }

                            s.setDouble(5, accuracy);

                            if (altitudeAccuracy == null) {
                                s.setObject(6, null);
                            } else {
                                s.setDouble(6, altitudeAccuracy);
                            }

                            if (heading == null) {
                                s.setObject(7, null);
                            } else {
                                s.setDouble(7, heading);
                            }

                            if (speed == null) {
                                s.setObject(8, null);
                            } else {
                                s.setDouble(8, speed);
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

    public void destroy() {
        executor.shutdown();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "servlet which handles location updates";
    }

}
