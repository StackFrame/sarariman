/*
 * Copyright (C) 2009-2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.clients.Client;
import com.stackframe.sarariman.clients.Clients;
import java.io.IOException;
import java.text.MessageFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class CustomerController extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private enum Action {

        create, update, delete
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
        Employee user = (Employee)request.getAttribute("user");
        if (!user.isAdministrator()) {
            response.sendError(401);
            return;
        }

        String name = request.getParameter("name");
        Action action = Action.valueOf(request.getParameter("action"));
        long id;
        Clients clients = sarariman.getClients();
        Client customer;
        switch (action) {
            case create:
                customer = clients.create(name);
                id = customer.getId();
                response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("customer?id={0}", id)));
                return;
            case update:
                id = Long.parseLong(request.getParameter("id"));
                customer = clients.getMap().get(id);
                customer.setName(name);
                response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("customer?id={0}", id)));
                return;
            case delete:
                id = Long.parseLong(request.getParameter("id"));
                customer = clients.getMap().get(id);
                customer.delete();
                response.sendRedirect(response.encodeRedirectURL("customers"));
                return;
            default:
                response.sendError(500);
                return;
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "performs updates on customers";
    }

}
