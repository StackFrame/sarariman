<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>PTO History</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>PTO History</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT * FROM paid_time_off WHERE employee=?
            <sql:param value="${param.employee}"></sql:param>
        </sql:query>

        <table id="pto" class="altrows">
            <tr><th>Amount</th><th>Comment</th><th>Date</th></tr>
            <c:forEach var="row" items="${resultSet.rows}">
                <tr>
                    <td class="duration">${row.amount}</td>
                    <td>${row.comment}</td>
                    <td>${row.created}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
