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
    <fmt:parseNumber var="project_id" value="${param.project}"/>
    <c:set var="project" value="${sarariman.projects[project_id]}"/>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Billing Report for ${fn:escapeXml(project.name)}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <c:url var="projectLink" value="project"><c:param name="id" value="${param.project}"/></c:url>

        <h1>Billing Report for <a href="${projectLink}">${fn:escapeXml(project.name)}</a></h1>

        <table class="altrows">
            <tr><th>Week</th><th>Hours</th><th>Billed</th></tr>
            <c:forEach var="week" items="${du:weekStarts(project.daysBilled)}">
                <tr>
                    <td><fmt:formatDate value="${week}" pattern="yyyy-MM-dd"/></td>
                    <sql:query dataSource="jdbc/sarariman" var="resultSet">
                        SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                        FROM hours AS h
                        JOIN tasks AS t on h.task = t.id
                        JOIN projects AS p on p.id = t.project
                        JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                        JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                        WHERE p.id = ? AND t.billable = TRUE AND h.date >= ? and h.date < DATE_ADD(?, INTERVAL 7 DAY);
                        <sql:param value="${param.project}"/>
                        <sql:param value="${week}"/>
                        <sql:param value="${week}"/>
                    </sql:query>

                    <c:forEach var="row" items="${resultSet.rows}">
                        <td class="duration">${row.durationTotal}</td>
                        <td class="currency"><fmt:formatNumber type="currency" value="${row.costTotal}"/></td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
