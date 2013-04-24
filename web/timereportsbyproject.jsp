<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>

    <c:choose>
        <c:when test="${!empty param.week}">
            <fmt:parseDate var="parsedWeek" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            <c:set var="week" value="${du:week(parsedWeek)}"/>
        </c:when>
        <c:otherwise>
            <c:set var="week" value="${du:week(du:now())}"/>
        </c:otherwise>
    </c:choose>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Projects - ${week}</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <h1>Projects - ${week}</h1>

            <form action="${request.requestURI}" method="get">
                <fmt:formatDate var="prevWeekString" value="${week.previous.start.time}" type="date" pattern="yyyy-MM-dd"/>
                <input type="submit" name="week" value="${prevWeekString}"/>
                <fmt:formatDate var="nextWeekString" value="${week.next.start.time}" type="date" pattern="yyyy-MM-dd"/>
                <input type="submit" name="week" value="${nextWeekString}"/>
            </form>

            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT DISTINCT p.id
                FROM hours as h
                JOIN tasks AS t ON h.task = t.id
                JOIN projects AS p ON t.project = p.id
                JOIN customers AS c ON c.id = p.customer
                WHERE h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY) AND h.duration > 0
                ORDER BY p.id ASC
                <sql:param value="${week.start.time}"/>
                <sql:param value="${week.start.time}"/>
            </sql:query>
            <ul>
                <c:forEach var="row" items="${result.rows}">
                    <c:set var="project" value="${sarariman.projects.map[row.id]}"/>
                    <c:if test="${sarariman:isManager(user, project) || sarariman:isCostManager(user, project) || user.administrator}">
                        <c:url var="target" value="projecttimereports">
                            <c:param name="project" value="${row.id}"/>
                            <c:param name="week" value="${week}"/>
                        </c:url>
                        <li>
                            <c:set var="customer" value="${project.client}"/>
                            <a href="${fn:escapeXml(target)}">${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</a>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
