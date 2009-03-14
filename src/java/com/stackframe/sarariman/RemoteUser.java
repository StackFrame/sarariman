package com.stackframe.sarariman;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A simple filter for overriding REMOTE_USER.
 *
 * @author mcculley
 */
public class RemoteUser implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest)request;
            if (httpServletRequest.getRemoteUser() == null) {
                final String username = System.getProperty("user.name");
                System.err.println("No REMOTE_USER.  Setting it to " + username);
                request = new HttpServletRequestWrapper(httpServletRequest) {

                    @Override
                    public String getRemoteUser() {
                        return username;
                    }

                };
            }
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
    }

}