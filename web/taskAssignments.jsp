<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Collection"%>
<%@page import="com.stackframe.sarariman.Sarariman"%>
<%@page import="com.stackframe.sarariman.Employee"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>
    <c:set var="employee" value="${directory.byNumber[param.employee]}"/>
    <head>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>${employee.fullName} Task Assignments</title>

        <script>
            $(document).ready(function(){
                $('.delete_task_assignment').click(function(e) {
                    $.ajax({
                        type: 'DELETE',
                        url: 'taskassignment?' + $(this).attr('data'),
                        async: false,
                        success: function() {
                            location.reload();
                        }
                    });
                });

                $('#add_task_assignment_button').click(function(e) {
                    $.ajax({
                        type: 'POST',
                        url: 'taskassignment',
                        data: $(this).attr('data') + "&task=" + $('#add_task_assignment').val(),
                        async: false,
                        success: function() {
                            location.reload();
                        }
                    });
                });
            });
        </script>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>
        <div class="container-fluid">
            <h1>${employee.fullName} Task Assignments</h1>

            <%
                Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
                Employee employee = (Employee)pageContext.getAttribute("employee");
                Collection<Integer> managers = sarariman.getOrganizationHierarchy().getManagers(employee.getNumber());
                pageContext.setAttribute("managers", managers);
            %>

            <ul>
                <c:forEach var="task" items="${employee.assignedTasks}">
                    <c:set var="taskAssignment" value="${sarariman.taskAssignments.map[employee][task]}"/>
                    <c:if test="${not empty task.project}">
                        <c:set var="isProjectManager" value="${sarariman:isManager(user, task.project)}"/>
                        <c:set var="isProjectCostManager" value="${sarariman:isCostManager(user, task.project)}"/>
                    </c:if>
                    <!-- FIXME: Add ACL for viewing task assignment. Must be on same project, task, or be a manager or admin. -->
                    <li id="task${task.id}">
                        <a href="${fn:escapeXml(taskAssignment.URL)}">${fn:escapeXml(task.name)} (${task.id})
                            <c:if test="${not empty task.project}">
                                - ${fn:escapeXml(task.project.name)} - ${fn:escapeXml(task.project.client.name)}
                            </c:if>
                        </a>
                        <c:if test="${isProjectManager || isProjectCostManager || user.administrator}">
                            <button class="btn delete_task_assignment" type="submit" name="task"
                                    data="employee=${employee.number}&task=${task.id}" title="remove this assignment">
                                <i class="icon-remove"></i>
                            </button>
                        </c:if>
                    </li>
                </c:forEach>
            </ul>

            <div class="input-append">
                <select name="task" id="add_task_assignment">
                    <sql:query dataSource="jdbc/sarariman" var="resultSet">
                        SELECT tasks.id, tasks.name, tasks.project
                        FROM tasks
                        LEFT JOIN projects ON projects.id = tasks.project
                        LEFT JOIN customers ON customers.id = projects.customer
                        WHERE tasks.id NOT IN
                        (SELECT task_assignments.task FROM task_assignments WHERE task_assignments.employee = ?)
                        AND tasks.active = TRUE
                        AND (projects.id IS NULL OR projects.active = TRUE)
                        AND (customers.id IS NULL OR customers.active = TRUE)
                        <sql:param value="${param.employee}"/>
                    </sql:query>
                    <c:forEach var="row" items="${resultSet.rows}">
                        <!-- FIXME: Add customer name. -->
                        <c:if test="${!empty row.project}">
                            <c:set var="project" value="${sarariman.projects.map[row.project]}"/>
                            <c:set var="isProjectManager" value="${sarariman:isManager(user, project)}"/>
                            <c:set var="isProjectCostManager" value="${sarariman:isCostManager(user, project)}"/>
                        </c:if>
                        <c:if test="${empty row.project || isProjectManager || isProjectCostManager || user.administrator}">
                            <option value="${row.id}">${row.id} - ${fn:escapeXml(row.name)}</option>
                        </c:if>
                    </c:forEach>
                </select>
                <button title="assign this task" class="btn" id="add_task_assignment_button" type="submit"
                        data="employee=${employee.number}">
                    <i class="icon-plus"></i>
                </button>
            </div>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
