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

/**
 * This is a hack to get around the limitations of the application container with regard to the default servlet and intercepting the
 * root URL.
 *
 * @author mcculley
 */
public class WebDAVRootFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        int lengthOfContextPath = contextPath.length();
        String requestedPath = requestURI.substring(lengthOfContextPath);
        if (requestedPath.equals("/")) {
            RootServlet rootServlet = new RootServlet();
            rootServlet.service(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    public void destroy() {
    }

}
