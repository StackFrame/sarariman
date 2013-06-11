/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class CardDAVDiscovery extends WebDAVServlet {

    @Override
    protected void doPropfind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Employee user = (Employee)request.getAttribute("user");
        // FIXME: This should now just go to /addressbooks
        response.sendRedirect(String.format("%s/staff/%s/addressbooks", request.getContextPath(), user.getUserName()));
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
