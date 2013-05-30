/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class PhotoFetcher extends HttpServlet {

    private static String entityTag(byte[] b) {
        HashFunction hashFunction = Hashing.md5();
        String hash = hashFunction.hashBytes(b).toString();
        return String.format("\"%s\"", hash);
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/jpeg");
        Directory directory = (Directory)getServletContext().getAttribute("directory");
        String uid = request.getParameter("uid");
        Employee employee = directory.getByUserName().get(uid);
        byte[] photo = employee.getPhoto();
        if (photo == null) {
            response.sendError(404);
        } else {
            String ETag = entityTag(photo);
            if (ETag.equals(request.getHeader("If-None-Match"))) {
                response.sendError(304);
            } else {
                response.addHeader("ETag", ETag);
                OutputStream out = response.getOutputStream();
                try {
                    out.write(photo);
                } finally {
                    out.close();
                }
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "serves up a photo for an employee";
    }

}
