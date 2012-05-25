<%--
  Copyright (C) 2009-2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <fmt:parseNumber var="customer_id" value="${param.id}"/>
    <c:set var="customer" value="${sarariman.customers[customer_id]}"/>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Customer ${customer.id}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Customer ${customer.id}</h1>
        <form method="POST" action="customerController">
            <label for="name">Name: </label>
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="id" value="${customer.id}"/>
            <input type="text" id="name" name="name" size="40" value="${fn:escapeXml(customer.name)}"/><br/>
            <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>

        <h2>Projects</h2>
        <table class="altrows" id="projects">
            <tr><th>ID</th><th>Name</th></tr>
            <c:forEach var="entry" items="${sarariman.projects}">
                <c:if test="${entry.value.customer == customer.id}">
                    <tr>
                        <td><a href="project?id=${entry.key}">${entry.key}</a></td>
                        <td><a href="project?id=${entry.key}">${fn:escapeXml(entry.value.name)}</a></td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>

        <c:url var="invoicesLink" value="invoicesByCustomer.jsp">
            <c:param name="customer" value="${param.id}"/>
        </c:url>
        <h2><a href="${invoicesLink}">Invoices</a></h2>


        <%@include file="footer.jsp" %>
    </body>
</html>
