<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>

    <c:set var="task_id" value="${param.task_id}"/>
    <fmt:parseNumber var="taskNumber" value="${param.task_id}"/>
    <c:set var="task" value="${sarariman.tasks.map[taskNumber]}"/>

    <c:if test="${user.administrator}">
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
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Task ${task_id}</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <h1>Task ${task_id}</h1>
            <form method="POST">
                <label for="task_name">Name: </label>
                <input type="text" id="task_name" name="task_name" value="${fn:escapeXml(task.name)}"/><br/>
                <label for="task_project">Project: </label>
                <select id="task_project" name="task_project">
                    <c:set var="project" value="${task.project}"/>
                    <option value="" <c:if test="${empty project}">selected="selected"</c:if>></option>
                    <c:forEach var="p" items="${sarariman.projects.all}">
                        <option value="${p.id}" <c:if test="${project == p}">selected="selected"</c:if>>${fn:escapeXml(p.name)}</option>
                    </c:forEach>
                </select><br/>
                <label for="billable">Billable: </label>
                <input type="checkbox" name="billable" id="billable" <c:if test="${task.billable}">checked="true"</c:if>
                       <c:if test="${!user.administrator}">disabled="true"</c:if>/>
                       <label for="active">Active: </label>
                       <input type="checkbox" name="active" id="active" <c:if test="${task.active}">checked="true"</c:if>
                       <c:if test="${!user.administrator}">disabled="true"</c:if>/>
                       <br/>
                       <label for="task_description">Description: </label>
                       <input type="text" size="80" id="task_description" name="task_description" value="${fn:escapeXml(task.description)}"/><br/>
                <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                </form>

            <c:set var="parent" value="${task.parent}"/>
            <c:if test="${not empty parent}">
                <p>Parent: <a href="${parent.URL}">${parent.id}: ${fn:escapeXml(parent.name)}</a></p>
            </c:if>

            <c:set var="children" value="${task.children}"/>
            <c:if test="${not empty children}">
                <h2>Child Tasks</h2>
                <ol>
                    <c:forEach var="child" items="${children}">
                        <li><a href="${child.URL}">${child.id}: ${fn:escapeXml(child.name)}</a></li>
                    </c:forEach>
                </ol>
            </c:if>

            <p>
                <c:url var="hoursByTask" value="hoursByTask">
                    <c:param name="task" value="${param.task_id}"/>
                </c:url>
                <a href="${hoursByTask}">Hours billed to this task</a>.
            </p>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
