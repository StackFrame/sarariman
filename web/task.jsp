<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <c:set var="task_id" value="${param.task_id}"/>
    <c:if test="${sarariman:isAdministrator(user)}">
        <c:choose>
            <c:when test="${!empty param.update}">
                <sql:update dataSource="jdbc/sarariman">
                    UPDATE tasks
                    SET name=?, billable=?, active=?, description=?, project=?
                    WHERE id=?
                    <sql:param value="${param.task_name}"/>
                    <sql:param value="${param.billable == 'on' ? 1 : 0}"/>
                    <sql:param value="${param.active == 'on' ? 1 : 0}"/>
                    <sql:param value="${param.task_description}"/>
                    <sql:param value="${empty param.task_project ? null : param.task_project}"/>
                    <sql:param value="${task_id}"/>
                </sql:update>
            </c:when>
            <c:when test="${!empty param.create}">
                <sql:transaction dataSource="jdbc/sarariman">
                    <sql:update>
                        INSERT INTO tasks
                        (name, billable, description, active, project)
                        VALUES(?, ?, ?, ?, ?);
                        <sql:param value="${param.task_name}"/>
                        <sql:param value="${param.billable == 'on' ? 1 : 0}"/>
                        <sql:param value="${param.task_description}"/>
                        <sql:param value="${param.active == 'on' ? 1 : 0}"/>
                        <sql:param value="${empty param.task_project ? null : param.task_project}"/>
                    </sql:update>
                    <sql:query var="insertResult">
                        SELECT LAST_INSERT_ID()
                    </sql:query>
                </sql:transaction>
                <c:set var="task_id" value="${insertResult.rowsByIndex[0][0]}"/>
            </c:when>
        </c:choose>
    </c:if>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Task ${task_id}</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>

        <sql:query dataSource="jdbc/sarariman" var="taskLookup">
            SELECT *
            FROM tasks
            WHERE id=?
            <sql:param value="${task_id}"/>
        </sql:query>
        <c:set var="task" value="${taskLookup.rows[0]}"/>

        <h1>Task ${task_id}</h1>
        <form method="POST">
            <label for="task_name">Name: </label>
            <input type="text" id="task_name" name="task_name" value="${fn:escapeXml(task.name)}"/><br/>
            <label for="task_project">Project: </label>
            <select id="task_project" name="task_project">
                <option value="" <c:if test="${empty task.project}">selected="selected"</c:if>></option>
                <sql:query dataSource="jdbc/sarariman" var="projects">
                    SELECT * FROM projects
                </sql:query>
                <c:forEach var="project" items="${projects.rows}">
                    <option value="${project.id}" <c:if test="${task.project == project.id}">selected="selected"</c:if>>${fn:escapeXml(project.name)}</option>
                </c:forEach>
            </select><br/>
            <label for="billable">Billable: </label>
            <input type="checkbox" name="billable" id="billable" <c:if test="${task.billable}">checked="true"</c:if>
                   <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/>
            <label for="active">Active: </label>
            <input type="checkbox" name="active" id="active" <c:if test="${task.active}">checked="true"</c:if>
                   <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/>
            <br/>
            <label for="task_description">Description: </label>
            <input type="text" size="80" id="task_description" name="task_description" value="${fn:escapeXml(task.description)}"/><br/>
            <input type="submit" name="update" value="Update" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <%@include file="footer.jsp" %>
    </body>
</html>
