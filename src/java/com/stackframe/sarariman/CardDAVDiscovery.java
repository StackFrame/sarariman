/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
public class CardDAVDiscovery extends HttpServlet {

    private Document makeDocument(String contextPath, String username) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.setXmlStandalone(true);

            Element multistatus = d.createElementNS("DAV:", "multistatus");
            d.appendChild(multistatus);

            Element response = d.createElementNS("DAV:", "response");
            multistatus.appendChild(response);

            Element propstat = d.createElementNS("DAV:", "propstat");
            response.appendChild(propstat);

            Element prop = d.createElementNS("DAV:", "prop");
            propstat.appendChild(prop);

            Element currentUserPrincipal = d.createElementNS("DAV:", "current-user-principal");
            prop.appendChild(currentUserPrincipal);

            Element href = d.createElementNS("DAV:", "href");
            currentUserPrincipal.appendChild(href);

            href.appendChild(d.createTextNode(String.format("%s/staff/%s", contextPath, username)));

            prop = d.createElementNS("DAV:", "prop");
            propstat.appendChild(prop);

            Element addressBookHomeSet = d.createElementNS("urn:ietf:params:xml:ns:carddav", "addressbook-home-set");
            prop.appendChild(addressBookHomeSet);

            href = d.createElementNS("DAV:", "href");
            addressBookHomeSet.appendChild(href);

            href.appendChild(d.createTextNode(String.format("%s/staff/%s/addressbooks/", contextPath, username)));

            Element status = d.createElementNS("DAV:", "status");
            propstat.appendChild(status);
            status.appendChild(d.createTextNode("HTTP/1.1 200 OK"));

            return d;
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
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

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if (!method.equals("PROPFIND")) {
            throw new ServletException("unexpected method '" + method + "'");
        }

        String requestDocument = CharStreams.toString(new InputStreamReader(request.getInputStream(), "UTF-8"));
        System.err.println("in CardDAVDiscovery::service requestDocument='" + requestDocument + "'");
        Employee user = (Employee)request.getAttribute("user");
        Document d = makeDocument(request.getContextPath(), user.getUserName());
        System.err.println("going to send response:");
        System.err.println(serialize(d));
        PrintWriter writer = response.getWriter();
        serialize(d, writer);
        writer.close();
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
