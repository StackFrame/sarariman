<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>PTO Usage</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->

    <fmt:parseDate var="week" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>

    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

        <h1>PTO usage for the week of ${thisWeekStart}</h1>

        <table class="altrows" id="timesheets">
            <tr><th>Employee</th><th>Used</th></tr>
            <c:forEach var="employeeEntry" items="${directory.byUserName}">
                <c:set var="employee" value="${employeeEntry.value}"/>
                <sql:query dataSource="jdbc/sarariman" var="resultSetUsed">
                    SELECT sum(amount) AS amount FROM paid_time_off WHERE employee=? AND effective=? AND amount < 0
                    <sql:param value="${employee.number}"/>
                    <sql:param value="${week}"/>
                </sql:query>
                <c:set var="used" value="${-resultSetUsed.rows[0].amount}"/>
                <tr>
                    <td>${employee.fullName}</td>
                    <td class="duration"><fmt:formatNumber value="${used}" minFractionDigits="2"/></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>