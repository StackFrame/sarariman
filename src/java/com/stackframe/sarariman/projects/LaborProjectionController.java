/*
 * Copyright (C) 2013 StackFrame, LLC
 * This code is licensed under GPLv2.
 */
package com.stackframe.sarariman.projects;

import com.stackframe.sarariman.AccessControlUtilities;
import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.PeriodOfPerformance;
import com.stackframe.sarariman.Sarariman;
import com.stackframe.sarariman.tasks.Task;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class LaborProjectionController extends HttpServlet {

    private Sarariman sarariman;

    private static int getProjectionId(HttpServletRequest request) {
        String s = request.getPathInfo();
        return Integer.parseInt(s.substring(s.lastIndexOf('/') + 1));
    }

    private LaborProjection getProjection(HttpServletRequest request) {
        return sarariman.getLaborProjections().get(getProjectionId(request));
    }

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
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
        System.err.println("LaborProjectionController::doGet");
        LaborProjection projection = getProjection(request);
        Project project = projection.getTask().getProject();
        Employee user = (Employee)request.getAttribute("user");
        boolean isCostManager = AccessControlUtilities.isCostManager(user, project);
        if (!isCostManager) {
            request.getRequestDispatcher("/unauthorized").forward(request, response);
        } else {
            request.setAttribute("projection", projection);
            request.setAttribute("project", project);
            request.getRequestDispatcher("/laborprojections/edit").include(request, response);
        }
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
        System.err.println("LaborProjectionController::doPost");
        if (request.getPathInfo().endsWith("/")) {
            // Handle creation of new entry here.
        } else {
            doPut(request, response);
        }
    }

    /**
     * Handles the HTTP
     * <code>PUT</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.err.println("LaborProjectionController::doPut");
        LaborProjection projection = getProjection(request);
        Project project = projection.getTask().getProject();
        Employee user = (Employee)request.getAttribute("user");
        boolean isCostManager = AccessControlUtilities.isCostManager(user, project);
        if (!isCostManager) {
            request.getRequestDispatcher("/unauthorized").forward(request, response);
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(request.getParameter("start"));
            Date end = dateFormat.parse(request.getParameter("end"));
            projection.setPeriodOfPerformance(new PeriodOfPerformance(start, end));

            double utilization = Double.parseDouble(request.getParameter("utilization"));
            projection.setUtilization(utilization);

            Task task = sarariman.getTasks().get(Integer.parseInt(request.getParameter("task")));
            projection.setTask(task);

            Employee employee = sarariman.getDirectory().getByNumber().get(Integer.parseInt(request.getParameter("employee")));
            projection.setEmployee(employee);

            response.sendRedirect(projection.getURL().toString());
        } catch (ParseException pe) {
            throw new ServletException(pe);
        }
    }

    /**
     * Handles the HTTP
     * <code>DELETE</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.err.println("LaborProjectionController::doDelete");
        LaborProjection projection = getProjection(request);
        System.err.println("projection=" + projection);
        Project project = projection.getTask().getProject();
        Employee user = (Employee)request.getAttribute("user");
        boolean isCostManager = AccessControlUtilities.isCostManager(user, project);
        if (!isCostManager) {
            request.getRequestDispatcher("/unauthorized").forward(request, response);
            return;
        }
        
        projection.delete();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Controller for labor projections";
    }

}
