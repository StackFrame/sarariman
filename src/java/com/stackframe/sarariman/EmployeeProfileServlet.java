/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Iterables;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.stackframe.sarariman.vcard.vCardRenderers;
import com.stackframe.sarariman.vcard.vCardSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    private static String entityTag(byte[] b) {
        HashFunction hashFunction = Hashing.md5();
        String hash = hashFunction.hashBytes(b).toString();
        return String.format("\"%s\"", hash);
    }

    private static String entityTag(String s) {
        return entityTag(s.getBytes());
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
            if (vCardMIMEType.equals(desiredMIMEType)) {
                response.setContentType(vCardMIMEType);
                String preferredFilename = employeeName + ".vcf";
                response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", preferredFilename));
                PrintWriter out = response.getWriter();
                try {
                    out.print(vCardRenderers.getvCardRenderer("3.0").render(employee.vCardSource()));
                } finally {
                    out.close();
                }
            } else if (xCardMIMEType.equals(desiredMIMEType)) {
                response.setContentType(xCardMIMEType);
                String preferredFilename = employeeName + ".xml";
                response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", preferredFilename));
                PrintWriter out = response.getWriter();
                try {
                    serialize(vCardRenderers.getxCardRenderer("4.0").render(employee.vCardSource()), out);
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

    private static Iterable<String> headers(HttpServletRequest request) {
        Collection<String> headers = new ArrayList<String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                headers.add(name + ": " + value);
            }
        }

        return headers;
    }

    public static String serialize(Document document) {
        StringWriter sw = new StringWriter();
        serialize(document, sw);
        return sw.toString();
    }

    // FIXME: This is broken and brittle. It returns an XML WebDAV document that describes the CardDAV address books available. Currently this is only the staff address book.
    private Document makeAddressBooksDocument(String contextPath, String username) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.setXmlStandalone(true);

            Element multistatus = d.createElementNS("DAV:", "multistatus");
            d.appendChild(multistatus);

            Element response = d.createElementNS("DAV:", "response");
            multistatus.appendChild(response);

            Element href = d.createElementNS("DAV:", "href");
            response.appendChild(href);

            href.appendChild(d.createTextNode(String.format("%s/staff/", contextPath)));

            Element propstat = d.createElementNS("DAV:", "propstat");
            response.appendChild(propstat);

            Element prop = d.createElementNS("DAV:", "prop");
            propstat.appendChild(prop);

            Element resourceType = d.createElementNS("DAV:", "resourcetype");
            prop.appendChild(resourceType);

            Element collection = d.createElementNS("DAV:", "collection");
            resourceType.appendChild(collection);

            Element addressBook = d.createElementNS("urn:ietf:params:xml:ns:carddav", "addressbook");
            resourceType.appendChild(addressBook);

            Element status = d.createElementNS("DAV:", "status");
            propstat.appendChild(status);
            status.appendChild(d.createTextNode("HTTP/1.1 200 OK"));

            response = d.createElementNS("DAV:", "response");
            multistatus.appendChild(response);

            propstat = d.createElementNS("DAV:", "propstat");
            response.appendChild(propstat);

            prop = d.createElementNS("DAV:", "prop");
            propstat.appendChild(prop);

            Element currentUserPrincipal = d.createElementNS("DAV:", "current-user-principal");
            prop.appendChild(currentUserPrincipal);

            href = d.createElementNS("DAV:", "href");
            currentUserPrincipal.appendChild(href);

            href.appendChild(d.createTextNode(String.format("%s/staff/%s", contextPath, username)));

            resourceType = d.createElementNS("DAV:", "resourcetype");
            prop.appendChild(resourceType);

            collection = d.createElementNS("DAV:", "collection");
            resourceType.appendChild(collection);

            prop = d.createElementNS("DAV:", "prop");
            propstat.appendChild(prop);

            Element principalURL = d.createElementNS("DAV:", "principal-URL");
            prop.appendChild(principalURL);

            href = d.createElementNS("DAV:", "href");
            principalURL.appendChild(href);

            href.appendChild(d.createTextNode(String.format("%s/staff/%s", contextPath, username)));

            prop = d.createElementNS("DAV:", "prop");
            propstat.appendChild(prop);

            Element addressBookHomeSet = d.createElementNS("urn:ietf:params:xml:ns:carddav", "addressbook-home-set");
            prop.appendChild(addressBookHomeSet);

            href = d.createElementNS("DAV:", "href");
            addressBookHomeSet.appendChild(href);

            href.appendChild(d.createTextNode(String.format("%s/staff/%s/addressbooks/", contextPath, username)));

            status = d.createElementNS("DAV:", "status");
            propstat.appendChild(status);
            status.appendChild(d.createTextNode("HTTP/1.1 200 OK"));

            return d;
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    // FIXME: This is broken and brittle. It returns an XML WebDAV document that describes the CardDAV resources available under /staff/
    private Document makeStaffDocument(String contextPath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.setXmlStandalone(true);

            Element multistatus = d.createElementNS("DAV:", "multistatus");
            d.appendChild(multistatus);

            for (Employee employee : activeEmployees()) {
                Element response = d.createElementNS("DAV:", "response");
                multistatus.appendChild(response);

                Element href = d.createElementNS("DAV:", "href");
                response.appendChild(href);

                href.appendChild(d.createTextNode(String.format("%s/staff/%s?type=text/vcard", contextPath,
                                                                employee.getUserName())));

                Element propstat = d.createElementNS("DAV:", "propstat");
                response.appendChild(propstat);

                Element prop = d.createElementNS("DAV:", "prop");
                propstat.appendChild(prop);

                Element resourceType = d.createElementNS("DAV:", "resourcetype");
                prop.appendChild(resourceType);

                Element getetag = d.createElementNS("DAV:", "getetag");
                prop.appendChild(getetag);
                String ETag = entityTag(vCardRenderers.getvCardRenderer("3.0").render(employee.vCardSource()));
                getetag.appendChild(d.createTextNode(String.format("%s", ETag)));

                Element status = d.createElementNS("DAV:", "status");
                propstat.appendChild(status);
                status.appendChild(d.createTextNode("HTTP/1.1 200 OK"));
            }

            return d;
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    // FIXME: This is broken and brittle and experimental.
    private void handlePROPFINDAddressBooks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.err.println("In addressbooks handler");
        String requestDocument = CharStreams.toString(new InputStreamReader(request.getInputStream(), "UTF-8"));
        System.err.println("requestDocument='" + requestDocument + "'");
        Employee user = (Employee)request.getAttribute("user");
        Document d = makeAddressBooksDocument(request.getContextPath(), user.getUserName());
        System.err.println("going to send response:");
        System.err.println(serialize(d));
        PrintWriter writer = response.getWriter();
        serialize(d, writer);
        writer.close();
        response.setStatus(207);
    }

    // FIXME: This is broken and brittle and experimental.
    private void handlePROPFINDRoot(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // FIXME: Need to figure out how to handle ctag.
        // FIXME: Need to figure out how to handle sync-token.
        // FIXME: Need to make sure that header depth == 1.
        System.err.println("In root handler");
        String requestDocument = CharStreams.toString(new InputStreamReader(request.getInputStream(), "UTF-8"));
        System.err.println("requestDocument='" + requestDocument + "'");
        Document d = makeStaffDocument(request.getContextPath());
        System.err.println("going to send response:");
        System.err.println(serialize(d));
        PrintWriter writer = response.getWriter();
        serialize(d, writer);
        writer.close();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("PROPFIND")) {
            String pathInfo = request.getPathInfo();

            System.err.println("Got a PROPFIND on the profile servlet path=" + request.getPathInfo());
            System.err.println("headers='" + headers(request) + "'");
            if (pathInfo.equals("/")) {
                handlePROPFINDRoot(request, response);
            } else {
                // FIXME: I am assuming here that the path is /staff/%user%/addressbooks. Check that this is what is actually requested.
                handlePROPFINDAddressBooks(request, response);
            }
        } else {
            super.service(request, response);
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
