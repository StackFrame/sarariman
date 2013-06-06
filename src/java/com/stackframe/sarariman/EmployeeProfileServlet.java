/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class EmployeeProfileServlet extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private static String employee(HttpServletRequest request) {
        String s = request.getPathInfo();
        return s.substring(s.lastIndexOf('/') + 1);
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String employeeName = employee(request);
        if (employeeName.isEmpty()) {
            request.getRequestDispatcher("/WEB-INF/staff/index.jsp").include(request, response);
        } else {
            Employee employee = sarariman.getDirectory().getByUserName().get(employeeName);
            request.setAttribute("employee", employee);

            // FIXME: This should take the locale of the user into account.
            request.setAttribute("formattedMobileNumber", PhoneNumberUtil.getInstance().format(employee.getMobile(), PhoneNumberFormat.NATIONAL));
            request.setAttribute("mobileNumberURI", PhoneNumberUtil.getInstance().format(employee.getMobile(), PhoneNumberFormat.RFC3966));

            Iterable<URL> profileLinks = employee.getProfileLinks();
            Collection<PrettyURL> prettyURLs = new ArrayList<PrettyURL>();
            for (URL u : profileLinks) {
                PrettyURL p = new PrettyURL(u);prettyURLs.add(p);
            }

            request.setAttribute("profiles", prettyURLs);

            request.getRequestDispatcher("/WEB-INF/staff/profile.jsp").include(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Employee Profile";
    }

}
