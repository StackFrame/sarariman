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
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        String action = request.getParameter("action");
        String name = request.getParameter("name");
        try {
            if (action.equals("create")) {
                Customer newCustomer = Customer.create(sarariman, name);
                long id = newCustomer.getId();
                response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("customer?id={0}", id)));
            } else if (action.equals("update")) {
                long id = Long.parseLong(request.getParameter("id"));
                Customer customer = sarariman.getCustomers().get(id);
                customer.setName(name);
                response.sendRedirect(response.encodeRedirectURL(MessageFormat.format("customer?id={0}", id)));
            } else if (action.equals("delete")) {
                long id = Long.parseLong(request.getParameter("id"));
                Customer customer = sarariman.getCustomers().get(id);
                customer.delete();
                response.sendRedirect(response.encodeRedirectURL("customers"));
            } else {
                response.sendError(500);
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
