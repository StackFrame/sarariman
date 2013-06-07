/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.TreeSet;
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
        String employeeName = s.substring(s.indexOf('/') + 1);
        int remainingPath = employeeName.indexOf('/');
        if (remainingPath != -1) {
            employeeName = employeeName.substring(0, remainingPath);
        }


        return employeeName;
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
            if (request.getPathInfo().endsWith("/vCard")) {
                response.setContentType("text/vcard");
                String preferredFilename = employeeName + ".vcf";
                response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", preferredFilename));
                PrintWriter out = response.getWriter();
                try {
                    out.print(employee.getVcard());
                } finally {
                    out.close();
                }
            } else {
                request.setAttribute("employee", employee);

                // FIXME: This should take the locale of the user into account.
                request.setAttribute("formattedMobileNumber", PhoneNumberUtil.getInstance().format(employee.getMobile(), PhoneNumberFormat.NATIONAL));
                request.setAttribute("mobileNumberURI", PhoneNumberUtil.getInstance().format(employee.getMobile(), PhoneNumberFormat.RFC3966));

                Iterable<URL> profileLinks = employee.getProfileLinks();
                Collection<PrettyURL> prettyURLs = new TreeSet<PrettyURL>();
                for (URL u : profileLinks) {
                    PrettyURL p = new PrettyURL(u);
                    prettyURLs.add(p);
                }

                request.setAttribute("profiles", prettyURLs);

                request.getRequestDispatcher("/WEB-INF/staff/profile.jsp").include(request, response);
            }
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
