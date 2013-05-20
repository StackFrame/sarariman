/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.geolocation;

/**
 * A representation of the Position interface defined by the W3 Geolocation API Specification
 * (http://www.w3.org/TR/geolocation-API/).
 *
 * @author mcculley
 */
public class Position {

    // These fields match those of the specification. They are provided as public final for ease of access and also with getters for
    // access as JavaBeans.
    public final Coordinates coords;

    public final long timestamp;

    public Position(Coordinates coords, long timestamp) {
        this.coords = coords;
        this.timestamp = timestamp;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Position{" + "coords=" + coords + ", timestamp=" + timestamp + '}';
    }

}
