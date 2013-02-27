<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Task Management</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Task Management</h1>

        <h2>Create a Task</h2>
        <form method="POST" action="task">
            <label for="task_name">Name: </label>
            <input type="text" id="task_name" name="task_name"/><br/>
            <label for="task_project">Project: </label>
            <select id="task_project" name="task_project">
                <option value=""></option>
                <sql:query dataSource="jdbc/sarariman" var="project_rows">
                    SELECT * FROM projects
                </sql:query>
                <c:forEach var="project_row" items="${project_rows.rows}">
                    <c:set var="project" value="${sarariman.projects.map[project_row.id]}"/>
                    <option value="${project_row.id}">${fn:escapeXml(project_row.name)} - ${fn:escapeXml(project.client.name)}</option>
                </c:forEach>
            </select><br/>
            <label for="billable">Billable: </label>
            <input type="checkbox" name="billable" id="billable" <c:if test="${!user.administrator}">disabled="true"</c:if>/>
            <label for="active">Active: </label>
            <input type="checkbox" name="active" id="active" <c:if test="${!user.administrator}">disabled="true"</c:if>/>
            <br/>
            <label for="task_description">Description: </label>
            <input type="text" size="80" id="task_description" name="task_description" value="${fn:escapeXml(task.description)}"/><br/>
            <input type="submit" name="create" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>
        <br/>

        <h2>Tasks</h2>
        <table class="altrows" id="tasks">
            <tr><th>ID</th><th>Name</th><th>Project</th><th>Customer</th></tr>
            <c:forEach var="task" items="${sarariman.tasks.all}">
                <tr>
                    <td><a href="task?task_id=${task.id}">${task.id}</a></td>
                    <td><a href="task?task_id=${task.id}">${fn:escapeXml(task.name)}</a></td>
                    <td><a href="task?task_id=${task.id}">${fn:escapeXml(task.project.name)}</a></td>
                    <td><a href="task?task_id=${task.id}">${fn:escapeXml(task.project.client.name)}</a></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
