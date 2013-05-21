/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.geolocation;

import static com.google.common.base.Preconditions.*;

/**
 * A representation of the Coordinates interface defined by the W3 Geolocation API Specification
 * (http://www.w3.org/TR/geolocation-API/).
 *
 * @author mcculley
 */
public class Coordinates {

    // These fields match those of the specification. They are provided as public final for ease of access and also with getters for
    // access as JavaBeans. Per the specification, altitude, altitudeAccuracy, heading, and speed may be null, so they are
    // represented as Double instead of double.
    public final double latitude;

    public final double longitude;

    public final Double altitude;

    public final double accuracy;

    public final Double altitudeAccuracy;

    public final Double heading;

    public final Double speed;

    public Coordinates(double latitude, double longitude, Double altitude, double accuracy, Double altitudeAccuracy, Double heading, Double speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        checkArgument(accuracy >= 0, "accuracy must be non-negative");
        this.accuracy = accuracy;
        checkArgument(altitudeAccuracy == null || altitudeAccuracy >= 0, "altitudeAccuracy must be non-negative");
        this.altitudeAccuracy = altitudeAccuracy;
        checkArgument(heading == null || (heading >= 0 && heading <= 360) || (Double.isNaN(heading) && speed == 0),
                      "heading must be in range [0, 360] or NaN if speed is 0");
        this.heading = heading;
        checkArgument(speed == null || speed >= 0, "speed must be non-negative");
        this.speed = speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public Double getAltitudeAccuracy() {
        return altitudeAccuracy;
    }

    public Double getHeading() {
        return heading;
    }

    public Double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Coordinates{" + "latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", accuracy=" + accuracy + ", altitudeAccuracy=" + altitudeAccuracy + ", heading=" + heading + ", speed=" + speed + '}';
    }

}
