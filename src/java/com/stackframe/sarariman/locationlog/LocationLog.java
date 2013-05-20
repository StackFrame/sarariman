/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.locationlog;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.geolocation.Coordinates;
import com.stackframe.sarariman.geolocation.Position;
import java.util.Map;

/**
 *
 * @author mcculley
 */
public interface LocationLog {

    void log(Employee employee, Coordinates position, String userAgent, String remoteAddress);

    Map<Employee, Position> getLatest();

}
