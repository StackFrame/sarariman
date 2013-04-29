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
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        final double latitude = Double.parseDouble(request.getParameter("latitude"));
        final double longitude = Double.parseDouble(request.getParameter("longitude"));
        final double altitude = Double.parseDouble(request.getParameter("altitude"));
        final double accuracy = Double.parseDouble(request.getParameter("accuracy"));
        final double altitudeAccuracy = Double.parseDouble(request.getParameter("altitudeAccuracy"));
        String headingString = request.getParameter("heading");
        final Double heading = headingString.equals("null") ? null : Double.parseDouble(headingString);

        String speedString = request.getParameter("speed");
        final Double speed = speedString.equals("null") ? null : Double.parseDouble(speedString);

        final String userAgent = httpServletRequest.getHeader("User-Agent");
        final String remoteAddress = httpServletRequest.getRemoteAddr();
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
                            s.setDouble(4, altitude);
                            s.setDouble(5, accuracy);
                            s.setDouble(6, altitudeAccuracy);
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
