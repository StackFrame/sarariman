<%--
  Copyright (C) 2010-2013 StackFrame, LLC
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
        <title>Billing Report for the week of ${param.week}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <fmt:parseDate var="weekParam" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
        <c:set var="week" value="${du:week(weekParam)}"/>
        <fmt:formatDate var="prevWeek" value="${week.previous.start.time}" type="date" pattern="yyyy-MM-dd"/>
        <c:url var="prevWeekURL" value="${request.requestURI}"><c:param name="week" value="${prevWeek}"/></c:url>
        <a href="${prevWeekURL}">${prevWeek}</a>
        <fmt:formatDate var="nextWeek" value="${week.next.start.time}" type="date" pattern="yyyy-MM-dd"/>
        <c:url var="nextWeekURL" value="${request.requestURI}"><c:param name="week" value="${nextWeek}"/></c:url>
        <a href="${nextWeekURL}">${nextWeek}</a>

        <h1>Billing Report for the week of ${param.week}</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
            FROM hours AS h
            JOIN tasks AS t on h.task = t.id
            JOIN projects AS p on p.id = t.project
            JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
            JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
            WHERE t.billable=TRUE AND h.date >= ? and h.date < DATE_ADD(?, INTERVAL 7 DAY);
            <sql:param value="${param.week}"/>
            <sql:param value="${param.week}"/>
        </sql:query>

        <c:forEach var="row" items="${resultSet.rows}">
            <p>Hours billed: ${row.durationTotal}.<br/>
                Total billed: <fmt:formatNumber type="currency" value="${row.costTotal}"/>.
            </p>
        </c:forEach>

        <ul>
            <c:forEach var="employeeEntry" items="${directory.byUserName}">
                <c:set var="employee" value="${employeeEntry.value}"/>
                <li>
                    <c:url var="employeeBilled" value="employeeBilled">
                        <c:param name="employee" value="${employee.number}"/>
                        <c:param name="week" value="${param.week}"/>
                    </c:url>
                    <a href="${fn:escapeXml(employeeBilled)}">${employee.fullName}</a>
                </li>
            </c:forEach>
        </ul>

        <c:url var="timeReports" value="timereportsbyproject"><c:param name="week" value="${param.week}"/></c:url>
        <a href="${timeReports}">Time reports for ${param.week}.</a>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT DISTINCT(p.id), p.name
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            JOIN customers AS c ON c.id = p.customer
            WHERE h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY) AND h.duration > 0
            ORDER BY p.id ASC
            <sql:param value="${param.week}"/>
            <sql:param value="${param.week}"/>
        </sql:query>

        <table class="altrows">
            <tr><th>Project</th></tr>
            <c:forEach var="row" items="${result.rows}">
                <tr>
                    <c:url var="projectLink" value="projectBilled">
                        <c:param name="project" value="${row.id}"/>
                    </c:url>
                    <td><a href="${fn:escapeXml(projectLink)}">${fn:escapeXml(row.name)}</a></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
