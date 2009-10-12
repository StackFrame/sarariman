<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="user" value="${directory.byUserName[pageContext.request.remoteUser]}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Task Management</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('tasks')">
        <p><a href="./">Home</a></p>
        <h1>Task Management</h1>

        <h2>Create a Task</h2>
        <form method="POST" action="task">
            <label for="task_name">Name: </label>
            <input type="text" id="task_name" name="task_name"/><br/>
            <label for="task_project">Project: </label>
            <select id="task_project" name="task_project">
                <option value=""></option>
                <sql:query dataSource="jdbc/sarariman" var="projects">
                    SELECT * FROM projects
                </sql:query>
                <c:forEach var="project" items="${projects.rows}">
                    <option value="${project.id}">${fn:escapeXml(project.name)}</option>
                </c:forEach>
            </select><br/>
            <label for="billable">Billable: </label>
            <input type="checkbox" name="billable" id="billable" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/>
            <label for="active">Active: </label>
            <input type="checkbox" name="active" id="active" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/>
            <br/>
            <label for="task_description">Description: </label>
            <input type="text" size="80" id="task_description" name="task_description" value="${fn:escapeXml(task.description)}"/><br/>
            <input type="submit" name="create" value="Create" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <br/>

        <h2>Tasks</h2>
        <table class="altrows" id="tasks">
            <tr><th>ID</th><th>Name</th><th>Project</th><th>Customer</th></tr>
            <c:set var="customers" value="${sarariman.customers}"/>
            <c:forEach var="task" items="${sarariman.tasks}">
                <tr>
                    <td><a href="task?task_id=${task.id}">${task.id}</a></td>
                    <td><a href="task?task_id=${task.id}">${fn:escapeXml(task.name)}</a></td>
                    <td><a href="task?task_id=${task.id}">${fn:escapeXml(task.project.name)}</a></td>
                    <td><a href="task?task_id=${task.id}">${fn:escapeXml(customers[task.project.customer].name)}</a></td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
