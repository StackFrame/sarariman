/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.geolocation.Coordinates;
import com.stackframe.sarariman.locationlog.LocationLog;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class LogLocation extends HttpServlet {

    private LocationLog locationLog;

    @Override
    public void init() throws ServletException {
        super.init();
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        locationLog = sarariman.getLocationLog();
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
        double latitude = Double.parseDouble(request.getParameter("latitude"));
        double longitude = Double.parseDouble(request.getParameter("longitude"));
        Double altitude = getDouble(request, "altitude");
        double accuracy = Double.parseDouble(request.getParameter("accuracy"));
        Double altitudeAccuracy = getDouble(request, "altitudeAccuracy");
        Double heading = getDouble(request, "heading");
        Double speed = getDouble(request, "speed");
        final Coordinates position = new Coordinates(latitude, longitude, altitude, accuracy, altitudeAccuracy, heading, speed);

        final String userAgent = request.getHeader("User-Agent");
        final String remoteAddress = request.getRemoteAddr();
        final Employee employee = (Employee)request.getAttribute("user");
        locationLog.log(employee, position, userAgent, remoteAddress);
    }

    @Override
    public void destroy() {
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
