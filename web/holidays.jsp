<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Holidays</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Holidays</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT date, description FROM holidays ORDER BY date
        </sql:query>
        <table>
            <tr><th>Date</th><th>Holiday</th></tr>
            <c:forEach var="row" items="${resultSet.rows}">
                <tr>
                    <td><fmt:formatDate value="${row.date}" type="date" pattern="MMM d" /></td>
                    <td>${row.description}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
