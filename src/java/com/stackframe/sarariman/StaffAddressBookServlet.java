/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.Iterables;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.stackframe.sarariman.vcard.vCardRenderers;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.ServletException;
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
public class StaffAddressBookServlet extends WebDAVServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private Iterable<Employee> activeEmployees() {
        Iterable<Employee> employees = sarariman.getDirectory().getByUserName().values();
        return Iterables.filter(employees, Utilities.active);
    }

    private static String entityTag(byte[] b) {
        HashFunction hashFunction = Hashing.md5();
        String hash = hashFunction.hashBytes(b).toString();
        return String.format("\"%s\"", hash);
    }

    private static String entityTag(String s) {
        return entityTag(s.getBytes());
    }

    private Document makePROPFINDResponseDepth0(String contextPath) {
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

            href.appendChild(d.createTextNode(String.format("%s/addressbooks/staff/", contextPath)));

            Element propstat = d.createElementNS("DAV:", "propstat");
            response.appendChild(propstat);

            Element prop = d.createElementNS("DAV:", "prop");
            propstat.appendChild(prop);

            Element getContentLength = d.createElementNS("DAV:", "getcontentlength");
            prop.appendChild(getContentLength);

            Element resourceType = d.createElementNS("DAV:", "resourcetype");
            prop.appendChild(resourceType);

            Element collection = d.createElementNS("DAV:", "collection");
            resourceType.appendChild(collection);

            Element creationDate = d.createElementNS("DAV:", "creationdate");
            prop.appendChild(creationDate);
            creationDate.appendChild(d.createTextNode(formattedDate()));

            Element getLastModified = d.createElementNS("DAV:", "getlastmodified");
            prop.appendChild(getLastModified);
            getLastModified.appendChild(d.createTextNode(formattedDate()));

            Element status = d.createElementNS("DAV:", "status");
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

                href.appendChild(d.createTextNode(String.format("%s/addressbooks/staff/%s.vcf", contextPath,
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

    private static String formattedDate() {
        return formattedDate(new Date());
    }

    private static String formattedDate(Date date) {
        // TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        //df.setTimeZone(tz);
        return df.format(date);
    }

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

    public static String serialize(Document document) {
        StringWriter sw = new StringWriter();
        serialize(document, sw);
        return sw.toString();
    }

    private Iterable<String> headers(HttpServletRequest request) {
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

    @Override
    protected void doPropfind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.err.println("in StaffAddressBookServlet::doPropfind pathInfo=" + request.getPathInfo() + " requestURI=" + request.getRequestURI());
        String requestDocument = CharStreams.toString(new InputStreamReader(request.getInputStream(), "UTF-8"));
        System.err.println("in StaffAddressBookServlet::doPropfind headers='" + headers(request) + "'");
        String depth = request.getHeader("depth");
        System.err.println("in StaffAddressBookServlet::doPropfind depth='" + depth + "'");
        System.err.println("in StaffAddressBookServlet::doPropfind requestDocument='" + requestDocument + "'");
        Document d;
        if (depth.equals("0")) {
            d = makePROPFINDResponseDepth0(request.getContextPath());
        } else {
            d = makeStaffDocument(request.getContextPath());
        }

        System.err.println("going to send response:");
        System.err.println(serialize(d));
        PrintWriter writer = response.getWriter();
        serialize(d, writer);
        writer.close();
        response.setStatus(207);
    }

}
