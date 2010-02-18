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

    <!-- FIXME: Get customer id out of config or look it up or something. -->
    <sql:query dataSource="jdbc/sarariman" var="customer" >
        SELECT id, name FROM customers WHERE id=?
        <sql:param value="1"/>
    </sql:query>

    <c:choose>
        <c:when test="${!empty param.week}">
            <c:set var="week" value="${param.week}"/>
        </c:when>
        <c:otherwise>
            <fmt:formatDate var="week" value="${du:weekStart(du:now())}" type="date" pattern="yyyy-MM-dd"/>
        </c:otherwise>
    </c:choose>
    
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>${customer.rows[0].name} - ${week}</title>
    </head>

    <body>
        <%@include file="../header.jsp" %>

        <h1>${customer.rows[0].name} - ${week}</h1>

        <form action="${request.requestURI}" method="get">
            <fmt:parseDate var="weekValue" value="${week}" pattern="yyyy-MM-dd"/>
            <fmt:formatDate var="prevWeekString" value="${du:prevWeek(weekValue)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${prevWeekString}"/>
            <fmt:formatDate var="nextWeekString" value="${du:nextWeek(weekValue)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${nextWeekString}"/>
        </form>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT DISTINCT p.name, p.id
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            JOIN customers AS c ON c.id = p.customer
            WHERE c.id = ? AND h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY)
            ORDER BY p.id ASC
            <sql:param value="${customer.rows[0].id}"/>
            <sql:param value="${week}"/>
            <sql:param value="${week}"/>
        </sql:query>
        <ul>
            <c:forEach var="row" items="${result.rows}">
                <c:url var="target" value="project.jsp">
                    <c:param name="project" value="${row.id}"/>
                    <c:param name="week" value="${week}"/>
                </c:url>
                <li><a href="${fn:escapeXml(target)}">${fn:escapeXml(row.name)}</a></li>
            </c:forEach>
        </ul>
        <%@include file="../footer.jsp" %>
    </body>
</html>
