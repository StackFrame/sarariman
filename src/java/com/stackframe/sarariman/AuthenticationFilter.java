/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import sun.misc.BASE64Decoder;

/**
 *
 * @author mcculley
 */
public class AuthenticationFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.err.println("entering AuthenticationFilter::doFilter");
        assert request instanceof HttpServletRequest;
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpSession session = httpRequest.getSession();
        Employee user = (Employee)session.getAttribute("user");
        if (user == null) {
            System.err.println("AuthenticationFilter::doFilter no user");
            String authorizationHeader = httpRequest.getHeader("Authorization");
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if (authorizationHeader == null) {
                httpResponse.addHeader("WWW-Authenticate", "Basic realm=\"sarariman\"");
                System.err.println("AuthenticationFilter::doFilter sending 401");
                httpResponse.sendError(401);
                return;
            } else {
                System.err.printf("Authorization='%s'", authorizationHeader);
                BASE64Decoder decoder = new BASE64Decoder();
                String[] split = authorizationHeader.split(" ");
                String encodedBytes = split[1];
                System.err.printf("encodedBytes='%s'", encodedBytes);
                byte[] decodedBytes = decoder.decodeBuffer(encodedBytes);
                String decodedString = new String(decodedBytes);
                System.err.printf("decodedString='%s'", decodedString);
                String[] credentials = decodedString.split(":");
                int firstColon = decodedString.indexOf(':');
                String username = decodedString.substring(0, firstColon);
                String password = decodedString.substring(firstColon + 1);
                System.err.printf("username='%s'", username);
                int domainIndex = username.indexOf('@');
                if (domainIndex == -1) {
                    httpResponse.sendError(401, "username must be in the form of an email address (user@example.com)");
                    return;
                }

                username = username.substring(0, domainIndex); // FIXME: Check for proper domain and dispatch.
                System.err.printf("username='%s'", username);
                System.err.printf("password='%s'", password);
                Sarariman sarariman = (Sarariman)request.getServletContext().getAttribute("sarariman");
                Directory directory = sarariman.getDirectory();
                boolean valid = directory.checkCredentials(username, password);
                System.err.printf("valid='%b'", valid);
                if (valid) {
                    System.err.println("AuthenticationFilter::doFilter valid!");
                    user = directory.getByUserName().get(username);
                    session.setAttribute("user", user);
                } else {
                    httpResponse.sendError(401, "invalid username or password");
                    return;
                }
            }
        } else {
            System.err.printf("user=%s\n", user.getUserName());
        }

        request.setAttribute("user", user);
        chain.doFilter(request, response);
        System.err.println("leaving AuthenticationFilter::doFilter");
    }

    public void destroy() {
    }

}
