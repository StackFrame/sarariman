/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.events;

import com.stackframe.sarariman.Linkable;
import java.net.URL;
import java.util.Date;

/**
 *
 * @author mcculley
 */
public interface Event extends Linkable {

    int getId();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    String getLocation();

    void setLocation(String location);

    Date getBegin();

    void setBegin(Date begin);

    Date getEnd();

    void setEnd(Date end);

    URL getLocationURL();

    void setLocationURL(URL locationURL);

    URL getLocationMapURL();

    void setLocationMapURL(URL locationURL);

}
