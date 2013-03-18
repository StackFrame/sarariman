<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<fmt:parseNumber var="task" value="${param.task}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Hours for task ${task}</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Hours for task ${task}</h1>
        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT * FROM hours WHERE task = ? ORDER BY date DESC
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
                    <td>${fn:escapeXml(row.description)}</td><%-- FIXME: Need to do something smarter here. --%>
                </tr>
                <c:set var="total" value="${total+row.duration}"/>
            </c:forEach>
            <tr><td colspan="2">Total</td><td class="duration">${total}</td><td></td></tr>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
