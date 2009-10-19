<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <fmt:parseNumber var="project_id" value="${param.id}"/>
    <c:set var="project" value="${sarariman.projects[project_id]}"/>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Project ${project.id}</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>

        <h1>Project ${project.id}</h1>
        <form method="POST" action="projectController">
            <label for="name">Name: </label>
            <input type="hidden" name="id" value="${project.id}"/>
            <input type="hidden" name="action" value="update"/>
            <input type="text" size="40" id="name" name="name" value="${fn:escapeXml(project.name)}"/><br/>
            <label for="customer">Customer: </label>
            <select id="customer" name="customer">
                <c:forEach var="entry" items="${sarariman.customers}">
                    <option value="${entry.key}" <c:if test="${entry.key == project.customer}">selected="selected"</c:if>>${fn:escapeXml(entry.value.name)}</option>
                </c:forEach>
            </select><br/>
            <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>
        <%@include file="footer.jsp" %>
    </body>
</html>
