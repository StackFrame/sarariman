/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A lot of the Sarariman code currently assumes that the "user" object is in the request object instead of the session object due
 * to the old way we did authentication. This filter just copies the user object from the session to the request. This class should
 * go away once all client code has been updated.
 *
 * @author mcculley
 */
public class UserFilter extends HttpFilter {

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute("user", request.getSession().getAttribute("user"));
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

}
