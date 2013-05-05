/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * It is very common that a Filter is only ever used for http requests. This class reduces some of the casting boilerplate necessary
 * for implementing a Filter.
 *
 * @author mcculley
 */
public abstract class HttpFilter implements Filter {

    protected abstract void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        assert request instanceof HttpServletRequest;
        assert response instanceof HttpServletResponse;
        doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

}
