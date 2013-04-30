/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.sarariman.taskassignments;

import com.stackframe.sarariman.Employee;
import com.stackframe.sarariman.Sarariman;
import com.stackframe.sarariman.tasks.Task;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mcculley
 */
public class TaskAssignmentController extends HttpServlet {

    private Sarariman sarariman;

    @Override
    public void init() throws ServletException {
        super.init();
        sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    }

    private TaskAssignment getAssignment(HttpServletRequest request) {
        int employeeNumber = Integer.parseInt(request.getParameter("employee"));
        int taskNumber = Integer.parseInt(request.getParameter("task"));
        Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
        Task task = sarariman.getTasks().get(taskNumber);
        TaskAssignment taskAssignment = sarariman.getTaskAssignments().get(employee, task);
        return taskAssignment;
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
        TaskAssignment taskAssignment = getAssignment(request);
        request.setAttribute("taskAssignment", taskAssignment);
        request.getRequestDispatcher("/taskAssignment.jsp").forward(request, response);
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
        // FIXME: Check that user is authorized.
        int employeeNumber = Integer.parseInt(request.getParameter("employee"));
        int taskNumber = Integer.parseInt(request.getParameter("task"));
        Employee employee = sarariman.getDirectory().getByNumber().get(employeeNumber);
        Task task = sarariman.getTasks().get(taskNumber);
        TaskAssignment taskAssignment = sarariman.getTaskAssignments().create(employee, task);
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
        // FIXME: Check that user is authorized.
        TaskAssignment taskAssignment = getAssignment(request);
        taskAssignment.delete();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Controller for task assignments";
    }

}
