/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.stackframe.sarariman.logincookies.LoginCookies;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author mcculley
 */
public class AuthCheck extends HttpServlet {

    private Directory directory;

    private LoginCookies loginCookies;

    @Override
    public void init() throws ServletException {
        super.init();
        Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
        directory = sarariman.getDirectory();
        loginCookies = sarariman.getLoginCookies();
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        username = username.toLowerCase();

        String fullyQualifiedUsername;
        int domainIndex = username.indexOf('@');
        if (domainIndex != -1) {
            fullyQualifiedUsername = username;
            username = username.substring(0, domainIndex); // FIXME: Check for proper domain and dispatch.
        } else {
            fullyQualifiedUsername = username + "@stackframe.com";
        }

        boolean valid = directory.checkCredentials(username, password);
        HttpSession session = request.getSession();
        if (valid) {
            Employee user = directory.getByUserName().get(username);
            session.setAttribute("user", user);

            session.setAttribute("authFailed", null);
            String destination = request.getParameter("destination");
            if (destination == null) {
                destination = request.getContextPath();
            }

            boolean rememberMe = "on".equals(request.getParameter("remember"));
            if (rememberMe) {
                try {
                    Cookie cookie = loginCookies.storeLoginToken(fullyQualifiedUsername, request);
                    response.addCookie(cookie);
                } catch (SQLException e) {
                    throw new ServletException(e);
                }
            }

            response.sendRedirect(destination);
        } else {
            session.setAttribute("authFailed", true);
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Handler for authentication";
    }

}
