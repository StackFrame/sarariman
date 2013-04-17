/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman.timesheets;

import com.stackframe.sarariman.AbstractLinkable;
import java.net.URI;

/**
 *
 * @author mcculley
 */
public class TimesheetsImpl extends AbstractLinkable implements Timesheets {

    private final String mountPoint;

    public TimesheetsImpl(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public URI getURI() {
        return URI.create(String.format("%stimesheets", mountPoint));
    }

}
