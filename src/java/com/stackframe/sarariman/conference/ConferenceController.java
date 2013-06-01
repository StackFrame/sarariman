/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.conference;

import com.stackframe.sarariman.Sarariman;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class ConferenceController extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private static String conferenceName(HttpServletRequest request) {
        String s = request.getPathInfo();
        return s.substring(s.lastIndexOf('/') + 1);
    }

    private Conference conference(HttpServletRequest request) {
        String conferenceName = conferenceName(request);
        return sarariman.getConferences().get(conferenceName);
    }

    /**
     * Processes requests for the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String conferenceName = conferenceName(request);
        if (conferenceName.isEmpty()) {
            request.getRequestDispatcher("/WEB-INF/conferences/index.jsp").include(request, response);
        } else {
            Conference conference = sarariman.getConferences().get(conferenceName);
            request.setAttribute("conference", conference);
            request.getRequestDispatcher("/WEB-INF/conferences/conference.jsp").include(request, response);
        }
    }

}
