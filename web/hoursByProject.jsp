<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<sql:query dataSource="jdbc/sarariman" var="resultSet">
    SELECT project FROM project_managers WHERE employee=? AND project=?
    <sql:param value="${user.number}"/>
    <sql:param value="${param.project}"/>
</sql:query>
<c:set var="isManager" value="${resultSet.rowCount == 1}"/>

<c:if test="${!(user.administrator || isManager)}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<fmt:parseNumber var="project" value="${param.project}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Hours for project ${task}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload ="altRows()">
        <%@include file="header.jsp" %>

        <h1>Hours for project ${task}</h1>
        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT h.task, h.date, h.employee, h.duration, h.description
            FROM hours AS h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            WHERE p.id = ?
            ORDER BY h.date DESC
            <sql:param value="${project}"/>
        </sql:query>
        <table class="altrows" id="hours">
            <tr><th>Date</th><th>Task</th><th>Employee</th><th>Duration</th><th>Description</th></tr>
            <c:set var="total" value="0.0"/>
            <c:forEach var="row" items="${result.rows}">
                <tr>
                    <td>${row.date}</td>
                    <td class="task">${row.task}</td>
                    <td>${directory.byNumber[row.employee].fullName}</td>
                    <td class="duration">${row.duration}</td>
                    <td>${row.description}</td>
                </tr>
                <c:set var="total" value="${total+row.duration}"/>
            </c:forEach>
            <tr><td colspan="3">Total</td><td class="duration">${total}</td><td></td></tr>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
