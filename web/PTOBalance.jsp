<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>PTO Balance</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>PTO Balance</h1>

        <table class="altrows" id="timesheets">
            <tr><th>Employee</th><th>Balance</th></tr>
            <c:forEach var="employeeEntry" items="${directory.byUserName}">
                <c:set var="employee" value="${employeeEntry.value}"/>
                <sql:query dataSource="jdbc/sarariman" var="resultSet">
                    SELECT sum(amount) AS balance FROM paid_time_off WHERE employee=?
                    <sql:param value="${employee.number}"/>
                </sql:query>
                <c:set var="balance" value="${resultSet.rows[0].balance}"/>
                <tr>
                    <td>${employee.fullName}</td>
                    <td class="duration"><fmt:formatNumber value="${balance}" minFractionDigits="2"/></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
