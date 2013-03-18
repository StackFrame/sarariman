<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<fmt:parseNumber var="customer_id" value="${param.customer}"/>
<c:set var="customer" value="${sarariman.customers[customer_id]}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Invoices for ${fn:escapeXml(customer.name)}</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <c:url var="customerLink" value="customer">
            <c:param name="id" value="${param.customer}"/>
        </c:url>
        <h1>Invoices for <a href="${customerLink}">${fn:escapeXml(customer.name)}</a></h1>

        <sql:query dataSource="jdbc/sarariman" var="invoices">
            SELECT DISTINCT(id) FROM invoice_info WHERE customer = ? ORDER BY id DESC
            <sql:param value="${customer_id}"/>
        </sql:query>

        <table id="invoices">
            <tr><th>Invoice</th><th>Sent</th><th>Project</th></tr>
            <c:forEach var="invoice" items="${invoices.rows}">
                <sql:query dataSource="jdbc/sarariman" var="invoice_info_result">
                    SELECT project, sent
                    FROM invoice_info AS i
                    WHERE i.id = ?
                    <sql:param value="${invoice.id}"/>
                </sql:query>
                <c:set var="invoice_info" value="${invoice_info_result.rows[0]}"/>
                <c:set var="project" value="${sarariman.projects[invoice_info.project]}"/>
                <tr>
                    <c:url var="link" value="invoice">
                        <c:param name="invoice" value="${invoice.id}"/>
                    </c:url>
                    <td><a href="${link}">${invoice.id}</a></td>
                    <fmt:formatDate var="sent" value="${invoice_info.sent}"/>

                    <c:choose>
                        <c:when test="${empty sent}">
                            <td class="error">no date</td>
                        </c:when>
                        <c:otherwise>
                            <td>${sent}</td>                            
                        </c:otherwise>
                    </c:choose>
                    <c:url var="projectLink" value="project">
                        <c:param name="id" value="${project.id}"/>
                    </c:url>
                    <td><a href="${projectLink}">${fn:escapeXml(project.name)}</a></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
