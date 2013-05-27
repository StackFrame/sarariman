/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.telephony;

import com.stackframe.sarariman.Sarariman;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executor;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author mcculley
 */
public class IncomingSMSHandler extends HttpServlet {

    private SMSGateway gateway;

    private Executor databaseExecutor;

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        gateway = sarariman.getSMSGateway();
        databaseExecutor = sarariman.getBackgroundDatabaseWriteExecutor();
        dataSource = sarariman.getDataSource();
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
        final String from = request.getParameter("from");
        System.err.println("from=" + from);
        final String to = request.getParameter("to");
        System.err.println("to=" + to);
        final String body = request.getParameter("body");
        System.err.println("body=" + body);
        final long now = System.currentTimeMillis();
        SMSEvent e = new SMSEvent(from, to, body, now);
        Runnable insertTask = new Runnable() {
            public void run() {
                try {
                    Connection c = dataSource.getConnection();
                    try {
                        PreparedStatement s = c.prepareStatement(
                                "INSERT INTO sms_log (from, to, body, timestamp) " +
                                "VALUES(?, ?, ?, ?)");
                        try {
                            s.setString(1, from);
                            s.setString(2, to);
                            s.setString(3, body);
                            s.setTimestamp(4, new Timestamp(now));
                            int numRowsInserted = s.executeUpdate();
                            assert numRowsInserted == 1;
                        } finally {
                            s.close();
                        }
                    } finally {
                        c.close();
                    }
                } catch (SQLException e) {
                    // FIXME: Should we log this exception? Does it kill the Executor?
                    throw new RuntimeException(e);
                }
            }

        };
        databaseExecutor.execute(insertTask);
        gateway.distribute(e);
    }

}
