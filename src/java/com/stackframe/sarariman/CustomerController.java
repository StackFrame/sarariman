/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import java.sql.SQLException;
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
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        Action a = Action.valueOf(request.getParameter("action"));
        Employee user = (Employee)request.getAttribute("user");
        if (!AccessControl.isAdministrator(user)) {
            response.sendError(401);
            return;
        }

        try {
            long id;
            Customer customer;
            switch (a) {
                case create:
                    customer = Customer.create(sarariman, name);
                    id = customer.getId();
                    response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("customer?id={0}", id)));
                    return;
                case update:
                    id = Long.parseLong(request.getParameter("id"));
                    customer = sarariman.getCustomers().get(id);
                    customer.setName(name);
                    response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("customer?id={0}", id)));
                    return;
                case delete:
                    id = Long.parseLong(request.getParameter("id"));
                    customer = sarariman.getCustomers().get(id);
                    customer.delete();
                    response.sendRedirect(response.encodeRedirectURL("customers"));
                    return;
                default:
                    response.sendError(500);
                    return;
            }
        } catch (SQLException se) {
            throw new IOException(se);
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
