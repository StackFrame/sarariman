<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<fmt:parseNumber var="task" value="${param.task}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Hours for task ${task}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload ="altRows('hours')">
        <p><a href="./">Home</a></p>
        <h1>Hours for task ${task}</h1>
        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT * FROM hours WHERE task = ?
            <sql:param value="${task}"/>
        </sql:query>
        <table id="hours">
            <tr><th>Date</th><th>Employee</th><th>Duration</th><th>Description</th></tr>
            <c:set var="total" value="0.0"/>
            <c:forEach var="row" items="${result.rows}">
                <tr>
                    <td>${row.date}</td>
                    <td>${directory.byNumber[row.employee].fullName}</td>
                    <td class="duration">${row.duration}</td>
                    <td>${row.description}</td>
                </tr>
                <c:set var="total" value="${total+row.duration}"/>
            </c:forEach>
            <tr><td colspan="2">Total</td><td class="duration">${total}</td><td></td></tr>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>