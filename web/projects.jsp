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
        <title>Projects</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('projects')">
        <p><a href="./">Home</a> <a href="tools">Tools</a></p>
        <h1>Projects</h1>

        <c:set var="customers" value="${sarariman.customers}"/>

        <h2>Create a new project</h2>
        <form method="POST" action="project">
            <label for="project_name">Name: </label>
            <input type="text" id="project_name" name="project_name" value="${fn:escapeXml(project.name)}"/><br/>
            <label for="project_customer">Customer: </label>
            <select id="project_customer" name="project_customer">
                <c:forEach var="entry" items="${customers}">
                    <option value="${entry.key}">${fn:escapeXml(entry.value.name)}</option>
                </c:forEach>
            </select><br/>
            <input type="submit" name="create" value="Create" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <br/>

        <table class="altrows" id="projects">
            <tr><th>ID</th><th>Name</th><th>Customer</th></tr>
            <c:forEach var="projectEntry" items="${sarariman.projects}">
                <tr>
                    <td><a href="project?id=${projectEntry.key}">${projectEntry.key}</a></td>
                    <td><a href="project?id=${projectEntry.key}">${fn:escapeXml(projectEntry.value.name)}</a></td>
                    <c:set var="customer_name" value="${fn:escapeXml(sarariman.customers[projectEntry.value.customer].name)}"/>
                    <td><a href="project?id=${projectEntry.key}">${customer_name}</a></td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
