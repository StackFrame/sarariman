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
        <title>Administrators</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('administrators')">
        <p><a href="./">Home</a> <a href="tools">Tools</a></p>
        <h1>Administrators</h1>

        <form method="POST" action="administratorController">
            <input type="hidden" name="action" value="add"/>
            <select id="employee" name="employee">
                <c:forEach var="entry" items="${directory.byUserName}">
                    <option value="${entry.value.number}">${fn:escapeXml(entry.value.fullName)}</option>
                </c:forEach>
            </select>
            <input type="submit" name="add" value="Add" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <br/>

        <table class="altrows" id="administrators">
            <tr>
                <th>Employee</th>
                <c:if test="${sarariman:isAdministrator(user)}"><th>Action</th></c:if>
            </tr>
            <c:forEach var="employee" items="${sarariman.administrators.administrators}">
                <tr>
                    <td>${employee.fullName}</td>
                    <c:if test="${sarariman:isAdministrator(user)}">
                        <td>
                            <form method="POST" action="administratorController">
                                <input type="hidden" name="action" value="remove"/>
                                <input type="hidden" name="id" value="${employee.number}"/>
                                <input type="submit" name="remove" value="Remove"/>
                            </form>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
