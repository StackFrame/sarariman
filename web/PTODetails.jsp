<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty param.employee}">
    <c:redirect url="${pageContext.request.servletPath}">
        <c:param name="employee" value="${user.number}"/>
    </c:redirect>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>PTO Details</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>PTO Details for ${directory.byNumber[param.employee].fullName}</h1>

        <sql:query dataSource="jdbc/sarariman" var="ptoResultSet">
            SELECT SUM(amount) AS total
            FROM paid_time_off
            WHERE employee = ?
            <sql:param value="${param.employee}"/>
        </sql:query>
        <p>Total Available: <span class="duration">${ptoResultSet.rows[0].total}</span> hours</p>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT * FROM paid_time_off WHERE employee=? ORDER BY effective DESC, created DESC
            <sql:param value="${param.employee}"></sql:param>
        </sql:query>

        <table id="pto" class="altrows">
            <tr><th>Amount</th><th>Comment</th><th>Date</th><th>Added</th></tr>
            <c:forEach var="row" items="${resultSet.rows}">
                <tr>
                    <td class="duration">${row.amount}</td>
                    <td>${row.comment}</td>
                    <td class="date">${row.effective}</td>
                    <td>${row.created}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
