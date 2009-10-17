<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="user" value="${directory.byUserName[pageContext.request.remoteUser]}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Customers</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('customers')">
        <p><a href="./">Home</a> <a href="tools">Tools</a></p>
        <h1>Customers</h1>

        <h2>Create a new customer</h2>
        <form method="POST" action="customer">
            <label for="customer_name">Name: </label>
            <input type="text" id="customer_name" name="customer_name" value=""/><br/>
            <input type="submit" name="create" value="Create" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <br/>

        <table class="altrows" id="customers">
            <tr><th>ID</th><th>Name</th></tr>
            <c:forEach var="customerEntry" items="${sarariman.customers}">
                <tr>
                    <td><a href="customer?id=${customerEntry.key}">${customerEntry.key}</a></td>
                    <td><a href="customer?id=${customerEntry.key}">${fn:escapeXml(customerEntry.value.name)}</a></td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
