/*
 * Copyright (C) 2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class PDFInvoiceBuilder extends HttpServlet {

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        Collection<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();
        String[] documentLinks = request.getParameterValues("documentLink");
        String[] documentNames = request.getParameterValues("documentName");
        for (int i = 0; i < documentLinks.length; i++) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(documentLinks[i]);
            ModifiableRequest tmpRequest = new ModifiableRequest(request);
            tmpRequest.setMethod("GET");
            ContentCaptureServletResponse capContent = new ContentCaptureServletResponse(response);
            requestDispatcher.include(tmpRequest, capContent);
            byte[] data = capContent.getContentBytes();
            MimeBodyPart mbp = new MimeBodyPart();
            try {
                mbp.setContent(data, capContent.getContentType());
                mbp.setFileName(documentNames[i]);
                attachments.add(mbp);
            } catch (MessagingException me) {
                IOException ioe = new IOException();
                ioe.initCause(me);
                throw ioe;
            }
        }

        StringBuilder body = new StringBuilder("Documents are attached for invoice ");
        body.append(request.getParameter("invoice") + ".\n");
        body.append("\n");

        Collection<InternetAddress> to = new ArrayList<InternetAddress>();
        for (String toAddress : request.getParameterValues("to")) {
            try {
                to.add(new InternetAddress(toAddress));
            } catch (AddressException ae) {
                throw new ServletException(ae);
            }
        }

        Collection<InternetAddress> cc = new ArrayList<InternetAddress>();
        String[] ccAddresses = request.getParameterValues("cc");
        if (ccAddresses != null) {
            for (String ccAddress : ccAddresses) {
                try {
                    cc.add(new InternetAddress(ccAddress));
                } catch (AddressException ae) {
                    throw new ServletException(ae);
                }
            }
        }

        String subject = "Invoice " + request.getParameter("invoice");

        sarariman.getEmailDispatcher().send(to, cc, subject, body.toString(), attachments);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Sent</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Sent</h1>");
            out.println("<p>email was sent.</p>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Builds PDF documents for invoices";
    }

}
