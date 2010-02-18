<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
    <fmt:parseNumber var="project_id" value="${param.project}"/>
    <c:set var="project" value="${sarariman.projects[project_id]}"/>
    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>${fn:escapeXml(customer.name)} - ${fn:escapeXml(project.name)} - ${param.week}</title>
    </head>

    <body>
        <%@include file="header.jsp" %>
        <h1>${fn:escapeXml(customer.name)} - ${fn:escapeXml(project.name)} - ${param.week}</h1>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT DISTINCT h.employee
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            WHERE p.id = ? AND h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY) AND h.duration > 0
            ORDER BY h.employee ASC
            <sql:param value="${param.project}"/>
            <sql:param value="${param.week}"/>
            <sql:param value="${param.week}"/>
        </sql:query>
        <ul>
            <c:forEach var="row" items="${result.rows}">
                <c:url var="target" value="timereport.jsp">
                    <c:param name="project" value="${param.project}"/>
                    <c:param name="week" value="${param.week}"/>
                    <c:param name="employee" value="${row.employee}"/>
                </c:url>
                <li><a href="${fn:escapeXml(target)}">${directory.byNumber[row.employee].fullName}</a></li>
            </c:forEach>
        </ul>
        <%@include file="footer.jsp" %>
    </body>
</html>
