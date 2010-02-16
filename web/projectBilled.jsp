<%--
  Copyright (C) 2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%
        Employee user = (Employee)request.getAttribute("user");
        if (!user.isInvoiceManager()) {
            response.sendError(401);
            return;
        }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Billing Report for project ${param.project} for the week of ${param.week}</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <fmt:parseDate var="week" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
        <fmt:formatDate var="prevWeek" value="${du:prevWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
        <c:url var="prevWeekURL" value="${request.requestURI}">
            <c:param name="week" value="${prevWeek}"/>
            <c:param name="project" value="${param.project}"/>
        </c:url>
        <a href="${fn:escapeXml(prevWeekURL)}">${prevWeek}</a>
        <fmt:formatDate var="nextWeek" value="${du:nextWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
        <c:url var="nextWeekURL" value="${request.requestURI}">
            <c:param name="week" value="${nextWeek}"/>
            <c:param name="project" value="${param.project}"/>
        </c:url>
        <a href="${fn:escapeXml(nextWeekURL)}">${nextWeek}</a>

        <h1>Billing Report for project ${param.project} for the week of ${param.week}</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
            FROM hours AS h
            JOIN tasks AS t on h.task = t.id
            JOIN projects AS p on p.id = t.project
            JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
            JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
            WHERE p.id=? AND t.billable=TRUE AND h.date >= ? and h.date < DATE_ADD(?, INTERVAL 7 DAY);
            <sql:param value="${param.project}"/>
            <sql:param value="${param.week}"/>
            <sql:param value="${param.week}"/>
        </sql:query>

        <c:forEach var="row" items="${resultSet.rows}">
            <p>Hours billed ${row.durationTotal}.<br/>
                Total billed <fmt:formatNumber type="currency" value="${row.costTotal}"/>.
            </p>
        </c:forEach>

        <c:url var="project" value="project"><c:param name="id" value="${param.project}"/></c:url>
        <a href="${project}">Project ${param.project}.</a>

        <%@include file="footer.jsp" %>
    </body>
</html>
