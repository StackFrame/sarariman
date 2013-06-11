/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.vcard.vCardRenderers;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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
public class StaffAddressBookEntryServlet extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private static final String vCardMIMEType = "text/vcard";

    private static final String vCardExtension = ".vcf";

    private static final String xCardMIMEType = "application/vcard+xml";

    private static final String xCardExtension = ".xml";

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
        String path = request.getPathInfo();
        if (path.endsWith(vCardExtension)) {
            String employeeName = request.getPathInfo().substring(1);
            response.setContentType(vCardMIMEType);
            employeeName = employeeName.substring(0, employeeName.length() - vCardExtension.length());
            Employee employee = sarariman.getDirectory().getByUserName().get(employeeName);
            PrintWriter out = response.getWriter();
            try {
                out.print(vCardRenderers.getvCardRenderer("3.0").render(employee.vCardSource()));
            } finally {
                out.close();
            }
        } else if (path.endsWith(xCardExtension)) {
            String employeeName = request.getPathInfo().substring(1);
            response.setContentType(xCardMIMEType);
            employeeName = employeeName.substring(0, employeeName.length() - xCardExtension.length());
            Employee employee = sarariman.getDirectory().getByUserName().get(employeeName);
            PrintWriter out = response.getWriter();
            try {
                serialize(vCardRenderers.getxCardRenderer("4.0").render(employee.vCardSource()), out);
            } finally {
                out.close();
            }
        } else {
            throw new FileNotFoundException("no handle for " + path);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "servlet for a vCard for an employee";
    }

}
