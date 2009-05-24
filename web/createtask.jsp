<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Create Task</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>

        <h1>Create Task</h1>
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
        <%@include file="footer.jsp" %>
    </body>
</html>
