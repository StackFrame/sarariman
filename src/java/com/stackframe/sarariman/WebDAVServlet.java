/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public abstract class WebDAVServlet extends HttpServlet {

    // FIXME: This does not yet handle all of the methods defined by WebDAV.

    protected void doPropfind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String protocol = request.getProtocol();
        String msg = "PROPFIND is not supported";
        if (protocol.endsWith("1.1")) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equals("PROPFIND")) {
            doPropfind(req, resp);
        } else {
            super.service(req, resp);
        }
    }

}
