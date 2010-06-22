<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
    <fmt:parseNumber var="project_id" value="${param.project}"/>
    <c:set var="project" value="${sarariman.projects[project_id]}"/>
    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Uninvoiced time for ${fn:escapeXml(customer.name)} - ${fn:escapeXml(project.name)}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body  onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Uninvoiced time for ${fn:escapeXml(customer.name)} - ${fn:escapeXml(project.name)}</h1>

        <c:if test="${user.administrator && !empty param.create}">
            <c:set var="createdInvoice" value="${sarariman:createInvoice(sarariman, customer, project, param.pop_start,
                                                 param.pop_end, pageContext.request.parameterMap, paramValues.addToInvoiceEmployee,
                                                 paramValues.addToInvoiceTask, paramValues.addToInvoiceDate)}"/>
            <p>Created <a href="invoice?invoice=${createdInvoice.id}">invoice ${createdInvoice.id}</a> with selected entries.</p>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT h.employee, h.task, h.date, h.duration
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            LEFT OUTER JOIN invoices AS i ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            WHERE p.id = ? AND i.id IS NULL AND h.duration > 0
            ORDER BY h.date ASC, h.employee ASC, h.task ASC
            <sql:param value="${param.project}"/>
        </sql:query>

        <c:choose>
            <c:when test="${result.rowCount > 0}">
                <fmt:parseDate var="pop_start" value="${result.rows[0].date}" pattern="yyyy-MM-dd"/>
                <c:set var="pop_start" value="${du:weekStart(pop_start)}"/>
            </c:when>
            <c:otherwise>
                <c:set var="pop_start" value="${du:weekStart(du:now())}"/>
            </c:otherwise>
        </c:choose>

        <c:set var="pop_end" value="${du:weekEnd(du:prevWeek(du:weekStart(du:now())))}"/>

        <form method="POST">
            <label>Create Invoice:</label><br/>

            <label for="pop_start">Period of Performance Start: </label>
            <input type="text" id="pop_start" name="pop_start" value="<fmt:formatDate value="${pop_start}" pattern="yyyy-MM-dd"/>"/>

            <label for="pop_end">End: </label>
            <input type="text" id="pop_end" name="pop_end" value="<fmt:formatDate value="${pop_end}" pattern="yyyy-MM-dd"/>"/><br/>

            <input type="submit" value="Create" name="create" <c:if test="${!user.administrator}">disabled="true"</c:if>/><br/>

            <table class="altrows">
                <tr><th>Employee</th><th>Task</th><th>Date</th><th>Duration</th><th>Invoice</th></tr>
                <c:forEach var="row" items="${result.rows}" varStatus="varStatus">
                    <fmt:parseDate var="date" value="${row.date}" pattern="yyyy-MM-dd"/>
                    <tr>
                        <fmt:formatDate var="timesheetWeek" value="${du:weekStart(row.date)}" pattern="yyyy-MM-dd"/>
                        <c:url var="timesheet" value="timesheet">
                            <c:param name="week" value="${timesheetWeek}"/>
                            <c:param name="employee" value="${row.employee}"/>
                        </c:url>
                        <td>${directory.byNumber[row.employee].fullName}</td>
                        <td>${row.task}</td>
                        <td><a href="${fn:escapeXml(timesheet)}">${row.date}</a></td>
                        <td><a href="${fn:escapeXml(timesheet)}">${row.duration}</a></td>
                        <td>
                            <input type="checkbox" name="addToInvoice${varStatus.index}" value="true"
                                   <c:if test="${date >= pop_start && date <= pop_end}">checked="true"</c:if>
                                   <c:if test="${!user.administrator}">disabled="true"</c:if>
                                   />
                        </td>
                    <input type="hidden" name="addToInvoiceEmployee" value="${row.employee}"/>
                    <input type="hidden" name="addToInvoiceTask" value="${row.task}"/>
                    <input type="hidden" name="addToInvoiceDate" value="${row.date}"/>
                    </tr>
                </c:forEach>
            </table>
        </form>

        <%@include file="footer.jsp" %>
    </body>
</html>
