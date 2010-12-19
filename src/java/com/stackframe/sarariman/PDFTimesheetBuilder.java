/*
 * Copyright (C) 2010 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
public class PDFTimesheetBuilder extends HttpServlet {

    private static Collection<InternetAddress> makeAddresses(String[] a) {
        if (a == null) {
            return new ArrayList<InternetAddress>();
        } else {
            return makeAddresses(new HashSet<String>(Arrays.asList(a)));
        }
    }

    private static Collection<InternetAddress> makeAddresses(Collection<String> c) {
        Collection<InternetAddress> result = new ArrayList<InternetAddress>();
        for (String s : c) {
            try {
                result.add(new InternetAddress(s));
            } catch (AddressException ae) {
                throw new IllegalArgumentException(ae);
            }
        }

        return result;
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        Collection<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();
        String[] pdfs = request.getParameterValues("pdf");
        String[] employees = request.getParameterValues("employee");
        for (int i = 0; i < pdfs.length; i++) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(pdfs[i]);
            ModifiableRequest tmpRequest = new ModifiableRequest(request);
            tmpRequest.setMethod("GET");
            ContentCaptureServletResponse capContent = new ContentCaptureServletResponse(response);
            requestDispatcher.include(tmpRequest, capContent);
            byte[] data = capContent.getContentBytes();
            MimeBodyPart mbp = new MimeBodyPart();
            try {
                mbp.setContent(data, capContent.getContentType());
                mbp.setFileName(String.format("%s-%s.pdf", employees[i], request.getParameter("week")));
                attachments.add(mbp);
            } catch (MessagingException me) {
                throw new IOException(me);
            }
        }

        StringBuilder body = new StringBuilder("Please find attached ");
        body.append(employees.length == 1 ? "the timesheet" : "timesheets");
        body.append(" for");
        if (employees.length == 1) {
            body.append(" " + employees[0] + ".\n");
        } else {
            body.append(":\n");
            for (String employee : employees) {
                body.append('\t' + employee + '\n');
            }
        }

        body.append("\n");
        body.append("Project: " + request.getParameter("project") + '\n');
        if (request.getParameter("contract") != null) {
            body.append("Contract: " + request.getParameter("contract") + '\n');
        }

        if (request.getParameter("subcontract") != null) {
            body.append("Subcontract: " + request.getParameter("subcontract") + '\n');
        }

        body.append("Week: " + request.getParameter("week") + '\n');
        body.append("\n");

        String[] noHoursEmployees = request.getParameterValues("noHoursEmployee");
        if (noHoursEmployees != null && noHoursEmployees.length > 0) {
            body.append("There were no hours this week for:\n");
            for (String noHoursEmployee : noHoursEmployees) {
                body.append('\t' + noHoursEmployee + '\n');
            }

            body.append("\n");
        }

        Collection<InternetAddress> to = new ArrayList<InternetAddress>();
        for (String toAddress : request.getParameterValues("to")) {
            try {
                to.add(new InternetAddress(toAddress));
            } catch (AddressException ae) {
                throw new ServletException(ae);
            }
        }

        Collection<InternetAddress> cc = makeAddresses(request.getParameterValues("cc"));

        final long projectNumber = Long.parseLong(request.getParameter("projectNumber"));
        final int employee = ((Employee)request.getAttribute("user")).getNumber();
        final String week = request.getParameter("week");

        String subject = employees.length == 1 ? "timesheet" : "timesheets";
        try {
            Project project = sarariman.getProjects().get((long)projectNumber);
            subject += " - " + project.getName();

            InternetAddress from;
            try {
                from = new InternetAddress(request.getParameter("from"));
            } catch (AddressException ae) {
                throw new ServletException(ae);
            }

            Runnable postSendAction = new Runnable() {

                public void run() {
                    Connection connection = sarariman.openConnection();
                    try {
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO project_timesheet_email_log (project, sender, week) VALUES(?, ?, ?)");
                        try {
                            ps.setInt(1, (int)projectNumber);
                            ps.setInt(2, employee);
                            ps.setString(3, week);
                            ps.executeUpdate();
                        } finally {
                            ps.close();
                            connection.close();
                        }
                    } catch (SQLException se) {
                        throw new RuntimeException(se);
                    }
                }

            };

            String testAddress = request.getParameter("testaddress");
            if (testAddress != null && testAddress.length() > 0) {
                postSendAction = null;
                try {
                    to = Collections.singleton(new InternetAddress(testAddress));
                    cc = null;
                } catch (AddressException ae) {
                    throw new ServletException(ae);
                }
            }

            sarariman.getEmailDispatcher().send(from, to, cc, subject, body.toString(), attachments, postSendAction);

            // FIXME: This should do a redirect to a GET with the confirmation.
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Email Sent</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Email Sent</h1>");
                out.println("<p>email was sent.</p>");
                out.println(String.format("<p>to=%s</p>", to));
                out.println(String.format("<p>cc=%s</p>", cc));
                out.println(String.format("<p>subject=%s</p>", subject));
                out.println(String.format("<p>body=%s</p>", body));
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        } catch (SQLException se) {
            throw new IOException(se);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Builds PDF documents for timesheets for a project";
    }

}
