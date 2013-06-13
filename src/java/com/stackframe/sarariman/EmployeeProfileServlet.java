/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Iterables;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.stackframe.sarariman.vcard.vCardRenderers;
import com.stackframe.sarariman.vcard.vCardSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

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

    private static final String vCardMIMEType = "text/vcard";

    private static final String xCardMIMEType = "application/vcard+xml";

    private static void serialize(Document document, Writer writer) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", new Integer(4));
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(writer);
            transformer.transform(new DOMSource(document), result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Iterable<Employee> activeEmployees() {
        Iterable<Employee> employees = sarariman.getDirectory().getByUserName().values();
        return Iterables.filter(employees, Utilities.active);
    }

    private Iterable<vCardSource> vCardSources() {
        Collection<vCardSource> vCardSources = new ArrayList<vCardSource>();
        for (Employee employee : activeEmployees()) {
            vCardSources.add(employee.vCardSource());
        }

        return vCardSources;
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
        String desiredMIMEType = request.getParameter("type");
        if (employeeName.isEmpty()) {
            if (vCardMIMEType.equals(desiredMIMEType)) {
                response.setContentType(vCardMIMEType);
                String preferredFilename = "staff.vcf";
                response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", preferredFilename));
                PrintWriter out = response.getWriter();
                try {
                    out.print(vCardRenderers.getvCardRenderer("3.0").render(vCardSources()));
                } finally {
                    out.close();
                }
            } else if (xCardMIMEType.equals(desiredMIMEType)) {
                response.setContentType(xCardMIMEType);
                String preferredFilename = "staff.xml";
                response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", preferredFilename));
                PrintWriter out = response.getWriter();
                try {
                    serialize(vCardRenderers.getxCardRenderer("4.0").render(vCardSources()), out);
                } finally {
                    out.close();
                }
            } else {
                request.getRequestDispatcher("/WEB-INF/staff/index.jsp").include(request, response);
            }
        } else {
            Employee employee = sarariman.getDirectory().getByUserName().get(employeeName);
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
