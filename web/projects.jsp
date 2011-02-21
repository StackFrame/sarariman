<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Projects</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Projects</h1>

        <c:set var="customers" value="${sarariman.customers}"/>

        <h2>Create a new project</h2>
        <form method="POST" action="projectController">
            <input type="hidden" name="action" value="create"/>
            <label for="name">Name: </label>
            <input type="text" size="40" id="name" name="name" value="${fn:escapeXml(project.name)}"/><br/>

            <label for="customer">Customer: </label>
            <select id="customer" name="customer">
                <c:forEach var="entry" items="${customers}">
                    <option value="${entry.key}">${fn:escapeXml(entry.value.name)}</option>
                </c:forEach>
            </select><br/>

            <label for="pop_start">Period of Performance Start: </label>
            <input type="text" id="pop_start" name="pop_start"/>

            <label for="pop_end">End: </label>
            <input type="text" id="pop_end" name="pop_end"/><br/>

            <input type="submit" name="create" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>
        <br/>

        <table class="altrows" id="projects">
            <tr><th>ID</th><th>Name</th><th>Customer</th>
                <c:if test="${user.administrator}"><th>Action</th></c:if>
            </tr>
            <c:forEach var="entry" items="${sarariman.projects}">
                <tr>
                    <td><a href="project?id=${entry.key}">${entry.key}</a></td>
                    <td><a href="project?id=${entry.key}">${fn:escapeXml(entry.value.name)}</a></td>
                    <c:set var="customer_name" value="${fn:escapeXml(sarariman.customers[entry.value.customer].name)}"/>
                    <td><a href="project?id=${entry.key}">${customer_name}</a></td>
                    <c:if test="${user.administrator}">
                        <td>
                            <form method="POST" action="projectController">
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
