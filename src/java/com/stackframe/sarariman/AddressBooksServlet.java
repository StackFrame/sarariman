/*
 * Copyright (C) 2013-2014 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.io.CharStreams;
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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mcculley
 */
public class AddressBooksServlet extends WebDAVServlet {

    private Document makePROPFINDResponseDepth0(String contextPath, String username) {
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

            href.appendChild(d.createTextNode(String.format("%s/addressbooks/", contextPath)));

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

//            prop = d.createElementNS("DAV:", "prop");
//            propstat.appendChild(prop);
//            Element currentUserPrincipal = d.createElementNS("DAV:", "current-user-principal");
//            prop.appendChild(currentUserPrincipal);
//            href = d.createElementNS("DAV:", "href");
//            currentUserPrincipal.appendChild(href);
//            href.appendChild(d.createTextNode(String.format("%s/staff/%s", contextPath, username)));
//            prop = d.createElementNS("DAV:", "prop");
//            propstat.appendChild(prop);
//            Element principalURL = d.createElementNS("DAV:", "principal-URL");
//            prop.appendChild(principalURL);
//            href = d.createElementNS("DAV:", "href");
//            principalURL.appendChild(href);
//            href.appendChild(d.createTextNode(String.format("%s/staff/%s", contextPath, username)));
//            prop = d.createElementNS("DAV:", "prop");
//            propstat.appendChild(prop);
            Element addressBookHomeSet = d.createElementNS("urn:ietf:params:xml:ns:carddav", "addressbook-home-set");
            prop.appendChild(addressBookHomeSet);

            href = d.createElementNS("DAV:", "href");
            addressBookHomeSet.appendChild(href);

            href.appendChild(d.createTextNode(String.format("%s/addressbooks/", contextPath)));

            Element status = d.createElementNS("DAV:", "status");
            propstat.appendChild(status);
            status.appendChild(d.createTextNode("HTTP/1.1 200 OK"));

            return d;
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    private Document makePROPFINDResponseDepth1(String requestURI, String contextPath) {
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

            Element displayname = d.createElementNS("DAV:", "displayname");
            prop.appendChild(displayname);
            displayname.appendChild(d.createTextNode("staff"));

            Element addressbookDescription = d.createElementNS("urn:ietf:params:xml:ns:carddav", "addressbook-description");
            prop.appendChild(addressbookDescription);
            addressbookDescription.appendChild(d.createTextNode("staff"));

            Element resourceType = d.createElementNS("DAV:", "resourcetype");
            prop.appendChild(resourceType);

            Element collection = d.createElementNS("DAV:", "collection");
            resourceType.appendChild(collection);

            Element addressBook = d.createElementNS("urn:ietf:params:xml:ns:carddav", "addressbook");
            resourceType.appendChild(addressBook);

            Element creationDate = d.createElementNS("DAV:", "creationdate");
            prop.appendChild(creationDate);
            creationDate.appendChild(d.createTextNode(formattedDate()));

            Element getLastModified = d.createElementNS("DAV:", "getlastmodified");
            prop.appendChild(getLastModified);
            getLastModified.appendChild(d.createTextNode(formattedDate()));

            Element getContentLength = d.createElementNS("DAV:", "getcontentlength");
            prop.appendChild(getContentLength);

            Element status = d.createElementNS("DAV:", "status");
            propstat.appendChild(status);
            status.appendChild(d.createTextNode("HTTP/1.1 200 OK"));

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
        transformerFactory.setAttribute("indent-number", 4);
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(writer);
            transformer.transform(new DOMSource(document), result);
        } catch (IllegalArgumentException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serialize(Document document) {
        StringWriter sw = new StringWriter();
        serialize(document, sw);
        return sw.toString();
    }

    private Iterable<String> headers(HttpServletRequest request) {
        Collection<String> headers = new ArrayList<>();
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
        System.err.println("in AddressBookServlet::doPropfind pathInfo=" + request.getPathInfo() + " requestURI=" + request.getRequestURI());
        String requestDocument = CharStreams.toString(new InputStreamReader(request.getInputStream(), "UTF-8"));
        System.err.println("in AddressBookServlet::doPropfind headers='" + headers(request) + "'");
        String depth = request.getHeader("depth");
        System.err.println("in AddressBookServlet::doPropfind depth='" + depth + "'");
        System.err.println("in AddressBookServlet::doPropfind requestDocument='" + requestDocument + "'");
        Employee employee = (Employee)request.getAttribute("user");
        Document d;
        if (depth.equals("0")) {
            d = makePROPFINDResponseDepth0(request.getContextPath(), employee.getUserName());
        } else {
            d = makePROPFINDResponseDepth1(request.getRequestURI(), request.getContextPath());
        }

        System.err.println("going to send response:");
        System.err.println(serialize(d));
        try (PrintWriter writer = response.getWriter();) {
            serialize(d, writer);
        }

        response.setStatus(207);
    }

}
