<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>SAIC Invoices</title>
    </head>
    <body>
        <%@include file="../header.jsp" %>

        <h1>SAIC Invoices</h1>
        <ul>
            <!-- FIXME: Get customer id out of config or look it up or something. -->
            <sql:query dataSource="jdbc/sarariman" var="invoices">
                SELECT DISTINCT i.id
                FROM invoices AS i
                JOIN tasks AS t ON t.id = i.task
                JOIN projects AS p on t.project = p.id
                JOIN customers AS c on c.id = p.customer
                WHERE c.id = 1
                ORDER BY i.id DESC
            </sql:query>
            <c:forEach var="invoice" items="${invoices.rows}">
                <li><a href="invoice.jsp?invoice=${invoice.id}">${invoice.id}</a></li>
            </c:forEach>
        </ul>
        <%@include file="../footer.jsp" %>
    </body>
</html>
