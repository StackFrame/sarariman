<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">


    <fmt:parseNumber var="project_id" value="${param.project}"/>
    <c:set var="project" value="${sarariman.projects.map[project_id]}"/>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Time Reports for Project ${project_id} ('${fn:escapeXml(project.name)}')</title>
    </head>

    <body>
        <%@include file="header.jsp" %>

        <h1>Time Reports for Project ${project_id} ('${fn:escapeXml(project.name)}')</h1>

        <ul>
            <c:forEach var="week" items="${project.workedWeeks}">
                <c:url var="link" value="projecttimereports">
                    <c:param name="project" value="${param.project}"/>
                    <c:param name="week" value="${week.name}"/>
                </c:url>
                <li><a href="${fn:escapeXml(link)}">${week.name}</a></li>
            </c:forEach>
        </ul>

        <%@include file="footer.jsp" %>
    </body>
</html>
