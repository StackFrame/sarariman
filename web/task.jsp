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
        <title>Task ${param.task_id}</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>

        <c:if test="${sarariman:isAdministrator(user) && !empty param.update}">
            <sql:update dataSource="jdbc/sarariman">
                UPDATE tasks
                SET name=?, billable=?, active=?, description=?
                WHERE id=?
                <sql:param value="${param.task_name}"/>
                <sql:param value="${param.billable == 'on' ? 1 : 0}"/>
                <sql:param value="${param.active == 'on' ? 1 : 0}"/>
                <sql:param value="${param.task_description}"/>
                <sql:param value="${param.task_id}"/>
            </sql:update>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="taskLookup">
            SELECT *
            FROM tasks
            WHERE id=?
            <sql:param value="${param.task_id}"/>
        </sql:query>
        <c:set var="task" value="${taskLookup.rows[0]}"/>

        <h1>Task ${param.task_id}</h1>
        <form method="POST">
            <label for="task_name">Name: </label>
            <input type="text" id="task_name" name="task_name" value="${task.name}"/><br/>
            <label for="billable">Billable: </label>
            <input type="checkbox" name="billable" id="billable" <c:if test="${task.billable}">checked="true"</c:if>
                   <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/>
            <label for="active">Active: </label>
            <input type="checkbox" name="active" id="active" <c:if test="${task.active}">checked="true"</c:if>
                   <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/>
            <br/>
            <label for="task_description">Description: </label>
            <input type="text" size="80" id="task_description" name="task_description" value="${task.description}"/><br/>
            <input type="submit" name="update" value="Update" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <%@include file="footer.jsp" %>
    </body>
</html>
