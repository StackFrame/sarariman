/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony;

import com.stackframe.sarariman.Sarariman;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class IncomingSMSHandler extends HttpServlet {

    private SMSGateway gateway;

    @Override
    public void init() throws ServletException {
        super.init();
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        gateway = sarariman.getSMSGateway();
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.err.println("entering IncomingSMSHandler::doPost");
        // FIXME: How do we verify this actually came from Twilio? IP address? Some token?
        String from = request.getParameter("From");
        System.err.println("from=" + from);
        String to = request.getParameter("To");
        System.err.println("to=" + to);
        String body = request.getParameter("Body");
        System.err.println("body=" + body);
        long now = System.currentTimeMillis();
        SMSEvent e = new SMSEvent(from, to, body, now);
        gateway.distribute(e);
    }

}
