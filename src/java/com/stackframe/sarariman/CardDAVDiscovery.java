/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class CardDAVDiscovery extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PROPFIND")) {
            throw new ServletException("unexpected method '" + method + "'");
        }

        String requestDocument = CharStreams.toString(new InputStreamReader(req.getInputStream(), "UTF-8"));
        System.err.println("in CardDAVDiscovery::service requestDocument='" + requestDocument + "'");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "CardDAV discovery hook";
    }

}
