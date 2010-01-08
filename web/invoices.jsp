<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Invoices</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('invoices')">
        <p><a href="./">Home</a></p>
        <h1>Invoices</h1>

        <!-- FIXME: use invoice_info directly once we have fixed all of the entries. -->

        <sql:query dataSource="jdbc/sarariman" var="invoices">
            SELECT DISTINCT id from invoices ORDER BY id DESC
        </sql:query>

        <table id="invoices">
            <tr><th>Invoice</th><th>Sent</th><th>Project</th><th>Customer</th></tr>
            <c:forEach var="invoice" items="${invoices.rows}">
                <tr>
                    <td><a href="invoice?invoice=${invoice.id}">${invoice.id}</a></td>
                    <sql:query dataSource="jdbc/sarariman" var="invoice_info_result">
                        SELECT *
                        FROM invoice_info AS i
                        WHERE i.id = ?
                        <sql:param value="${invoice.id}"/>
                    </sql:query>
                    <c:set var="invoice_info" value="${invoice_info_result.rows[0]}"/>
                    <c:set var="project" value="${sarariman.projects[invoice_info.project]}"/>
                    <c:set var="customer" value="${sarariman.customers[invoice_info.customer]}"/>
                    <fmt:formatDate var="sent" value="${invoice_info.sent}"/>

                    <c:choose>
                        <c:when test="${empty sent}">
                            <td class="error">no date</td>
                        </c:when>
                        <c:otherwise>
                            <td>${sent}</td>                            
                        </c:otherwise>
                    </c:choose>
                    <td>${fn:escapeXml(project.name)}</td>
                    <td>${fn:escapeXml(customer.name)}</td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
