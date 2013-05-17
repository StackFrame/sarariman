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

    <c:set var="employee" value="${directory.byNumber[param.employee]}"/>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Billing Report for ${employee.fullName} for the week of ${param.week}</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <fmt:parseDate var="weekParam" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            <c:set var="week" value="${du:week(weekParam)}"/>
            <fmt:formatDate var="prevWeek" value="${week.previous.start.time}" type="date" pattern="yyyy-MM-dd"/>
            <c:url var="prevWeekURL" value="${request.requestURI}">
                <c:param name="employee" value="${param.employee}"/>
                <c:param name="week" value="${prevWeek}"/>
            </c:url>
            <a href="${fn:escapeXml(prevWeekURL)}">${prevWeek}</a>
            <fmt:formatDate var="nextWeek" value="${week.next.start.time}" type="date" pattern="yyyy-MM-dd"/>
            <c:url var="nextWeekURL" value="${request.requestURI}">
                <c:param name="employee" value="${param.employee}"/>
                <c:param name="week" value="${nextWeek}"/>
            </c:url>
            <a href="${fn:escapeXml(nextWeekURL)}">${nextWeek}</a>

            <h1>Billing Report for ${employee.fullName} for the week of ${param.week}</h1>

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                FROM hours AS h
                JOIN tasks AS t on h.task = t.id
                JOIN projects AS p on p.id = t.project
                JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                WHERE h.employee=? AND t.billable=TRUE AND h.date >= ? and h.date < DATE_ADD(?, INTERVAL 7 DAY);
                <sql:param value="${param.employee}"/>
                <sql:param value="${param.week}"/>
                <sql:param value="${param.week}"/>
            </sql:query>

            <c:forEach var="row" items="${resultSet.rows}">
                <p>Hours billed: ${row.durationTotal}.<br/>
                    Total billed: <fmt:formatNumber type="currency" value="${row.costTotal}"/>.
                </p>
            </c:forEach>

            <c:url var="timesheet" value="timesheet"><c:param name="employee" value="${param.employee}"/><c:param name="week" value="${param.week}"/></c:url>
            <a href="${fn:escapeXml(timesheet)}">Timesheet for ${employee.fullName} for ${param.week}.</a>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
