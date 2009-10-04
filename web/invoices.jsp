<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<c:set var="user" value="${directory.byUserName[pageContext.request.remoteUser]}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Invoices</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>
        <h1>Invoices</h1>
        <ul>
            <sql:query dataSource="jdbc/sarariman" var="invoices">
                SELECT DISTINCT id from invoices ORDER BY id DESC
            </sql:query>
            <c:forEach var="invoice" items="${invoices.rows}">
                <li><a href="invoice?invoice=${invoice.id}">${invoice.id}</a></li>
            </c:forEach>
        </ul>
        <%@include file="footer.jsp" %>
    </body>
</html>
