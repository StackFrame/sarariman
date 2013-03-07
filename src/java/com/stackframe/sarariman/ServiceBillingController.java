/*
 * Copyright (C) 2011-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import static com.stackframe.sql.SQLUtilities.convert;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateMidnight;

/**
 *
 * @author mcculley
 */
public class ServiceBillingController extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private enum Action {

        create
    }

    private void createBilling(int serviceAgreement, DateMidnight popStart, DateMidnight popEnd) throws ServletException {
        Connection connection = sarariman.openConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO billed_services (service_agreement, pop_start, pop_end) VALUES(?, ?, ?)");
            try {
                ps.setInt(1, serviceAgreement);
                ps.setDate(2, convert(popStart.toDate()));
                ps.setDate(3, convert(popEnd.toDate()));
                ps.executeUpdate();
            } finally {
                ps.close();
                connection.close();
            }
        } catch (SQLException se) {
            throw new ServletException(se);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Employee user = (Employee)request.getAttribute("user");
        if (!user.isAdministrator()) {
            response.sendError(401);
            return;
        }

        Action action = Action.valueOf(request.getParameter("action"));
        try {
            int serviceAgreement = Integer.parseInt(request.getParameter("service_agreement"));
            DateMidnight popStart = new DateMidnight(request.getParameter("pop_start"));
            DateMidnight popEnd = new DateMidnight(request.getParameter("pop_end"));
            switch (action) {
                case create:
                    createBilling(serviceAgreement, popStart, popEnd);
                    break;
                default:
                    response.sendError(500);
                    return;
            }

            response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("serviceagreement?id={0}", serviceAgreement)));
            return;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /** 
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "performs operations on the billed_services table";
    }

}
