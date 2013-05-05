/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import sun.misc.BASE64Decoder;

/**
 *
 * @author mcculley
 */
public class AuthenticationFilter extends HttpFilter {

    private Directory directory;

    public void init(FilterConfig filterConfig) throws ServletException {
        Sarariman sarariman = (Sarariman)filterConfig.getServletContext().getAttribute("sarariman");
        directory = sarariman.getDirectory();
    }

    /**
     * Paths to resources which do not require authentication.
     *
     * FIXME: This should come from a config file. FIXME: This should be a set of regex patterns.
     */
    private static final Set<String> publicPaths = ImmutableSet.of("/login", "/auth_check", "/css/bootstrap.css", "/favicon.ico",
                                                                   "/css/bootstrap-responsive.css", "/style/font-awesome.css",
                                                                   "/jquery/js/jquery-1.7.2.min.js", "/js/bootstrap.js",
                                                                   "/font/fontawesome-webfont.ttf",
                                                                   "/font/fontawesome-webfont.woff");

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession session = request.getSession();
        Employee user = (Employee)session.getAttribute("user");
        if (user == null) {
            String authorizationHeader = request.getHeader("Authorization");
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if (authorizationHeader == null) {
                String requestPath = request.getServletPath();
                if (publicPaths.contains(requestPath)) {
                    chain.doFilter(request, response);
                } else {
                    String userAgent = request.getHeader("User-Agent");

                    // FIXME: This is an ugly hack to deal with a particular client that needs to do Basic auth.
                    if (userAgent.startsWith("Status%20Board/")) {
                        httpResponse.addHeader("WWW-Authenticate", "Basic realm=\"sarariman\"");
                        httpResponse.sendError(401);
                    } else {
                        String destination = request.getRequestURI();
                        String queryString = request.getQueryString();
                        if (queryString != null) {
                            destination = destination + '?' + queryString;
                        }

                        // FIXME: If the method requested was a POST, redirect to home page or something.

                        String redirectURL = String.format("%s/login", request.getContextPath());
                        String defaultDestination = request.getContextPath() + "/";
                        if (!destination.equals(defaultDestination)) {
                            redirectURL = redirectURL + String.format("?destination=%s", URLEncoder.encode(destination, "UTF-8"));
                        }

                        httpResponse.sendRedirect(redirectURL);
                    }
                }
            } else {
                BASE64Decoder decoder = new BASE64Decoder();
                String[] split = authorizationHeader.split(" ");
                String encodedBytes = split[1];
                byte[] decodedBytes = decoder.decodeBuffer(encodedBytes);
                String decodedString = new String(decodedBytes);
                int firstColon = decodedString.indexOf(':');
                String username = decodedString.substring(0, firstColon);
                username = username.toLowerCase();
                String password = decodedString.substring(firstColon + 1);
                int domainIndex = username.indexOf('@');
                if (domainIndex != -1) {
                    username = username.substring(0, domainIndex); // FIXME: Check for proper domain and dispatch.
                }

                boolean valid = directory.checkCredentials(username, password);
                if (valid) {
                    user = directory.getByUserName().get(username);
                    session.setAttribute("user", user);

                    // FIXME: All of the client code expects that the request holds the user object. Maybe change client code to use
                    // the session?
                    request.setAttribute("user", user);
                    chain.doFilter(request, response);
                } else {
                    httpResponse.sendError(401, "invalid username or password");
                }
            }
        } else {
            // FIXME: All of the client code expects that the request holds the user object. Maybe change client code to use
            // the session?
            request.setAttribute("user", user);
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }

}
