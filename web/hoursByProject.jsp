<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<fmt:parseNumber var="project_id" value="${param.project}"/>
<c:set var="project" value="${sarariman.projects.map[project_id]}"/>

<c:set var="isManager" value="${sarariman:isManager(user, project)}"/>
<c:set var="isCostManager" value="${sarariman:isCostManager(user, project)}"/>

<c:if test="${!(user.administrator || isManager || isCostManager)}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Hours for ${fn:escapeXml(project.name)}</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <h1>Hours for ${fn:escapeXml(project.name)}</h1>
            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT h.task, h.date, h.employee, h.duration, h.description
                FROM hours AS h
                JOIN tasks AS t ON h.task = t.id
                JOIN projects AS p ON t.project = p.id
                WHERE p.id = ?
                ORDER BY h.date DESC
                <sql:param value="${project_id}"/>
            </sql:query>
            <table id="hours">
                <tr><th>Date</th><th>Task</th><th>Employee</th><th>Duration</th><th>Description</th></tr>
                <c:set var="total" value="0.0"/>
                <c:forEach var="row" items="${result.rows}">
                    <tr>
                        <td class="date">${row.date}</td>
                        <td class="task">${row.task}</td>
                        <td>${directory.byNumber[row.employee].fullName}</td>
                        <td class="duration"><fmt:formatNumber value="${row.duration}" minFractionDigits="2"/></td>
                        <td>${row.description}</td>
                    </tr>
                    <c:set var="total" value="${total + row.duration}"/>
                </c:forEach>
                <tr>
                    <td colspan="3">Total</td>
                    <td class="duration"><fmt:formatNumber value="${total}" minFractionDigits="2"/></td>
                    <td></td>
                </tr>
            </table>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
