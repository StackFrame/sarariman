<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
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
        <form method="POST" action="customerController">
            <label for="name">Name: </label>
            <input type="text" size="40" id="name" name="name" value=""/><br/>
            <input type="hidden" name="action" value="create"/>
            <input type="submit" name="create" value="Create" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <br/>

        <table class="altrows" id="customers">
            <tr><th>ID</th><th>Name</th>
                <c:if test="${sarariman:isAdministrator(user)}"><th>Action</th></c:if>
            </tr>
            <c:forEach var="entry" items="${sarariman.customers}">
                <tr>
                    <td><a href="customer?id=${entry.key}">${entry.key}</a></td>
                    <td><a href="customer?id=${entry.key}">${fn:escapeXml(entry.value.name)}</a></td>
                    <c:if test="${sarariman:isAdministrator(user)}">
                        <td>
                            <form method="POST" action="customerController">
                                <input type="hidden" name="action" value="delete"/>
                                <input type="hidden" name="id" value="${entry.key}"/>
                                <input type="submit" name="delete" value="Delete"/>
                            </form>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
