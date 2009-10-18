<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <fmt:parseNumber var="customer_id" value="${param.id}"/>
    <c:set var="customer" value="${sarariman.customers[customer_id]}"/>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Customer ${customer.id}</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>

        <h1>Customer ${customer.id}</h1>
        <form method="POST" action="customerController">
            <label for="name">Name: </label>
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="id" value="${customer.id}"/>
            <input type="text" id="name" name="name" value="${fn:escapeXml(customer.name)}"/><br/>
            <input type="submit" name="update" value="Update" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <%@include file="footer.jsp" %>
    </body>
</html>
