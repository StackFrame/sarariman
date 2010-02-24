/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author mcculley
 */
public class ModifiableRequest extends HttpServletRequestWrapper {

    public String method;

    public ModifiableRequest(HttpServletRequest req) {
        super(req);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getMethod() {
        return method;
    }

}
