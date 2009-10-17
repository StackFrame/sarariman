<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <c:choose>
        <c:when test="${!empty param.employee}">
            <c:set var="employee" value="${directory.byNumber[param.employee]}"/>
        </c:when>
        <c:otherwise>
            <c:set var="employee" value="${user}"/>
        </c:otherwise>
    </c:choose>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>${employee.fullName}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->
    <body onload="altRows('timesheet')">
        <p><a href="./">Home</a></p>

        <c:choose>
            <c:when test="${!empty param.week}">
                <fmt:parseDate var="week" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            </c:when>
            <c:otherwise>
                <c:set var="week" value="${du:weekStart(du:now())}"/>
            </c:otherwise>
        </c:choose>

        <form action="${request.requestURI}" method="get">
            <fmt:formatDate var="prevWeekString" value="${du:prevWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${prevWeekString}"/>
            <fmt:formatDate var="nextWeekString" value="${du:nextWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${nextWeekString}"/>
            <input type="hidden" name="employee" value="${employee.number}"/>
        </form>
        <form>
            <label for="employee">Employee: </label>
            <select name="employee" id="employee">
                <c:forEach var="e" items="${directory.byUserName}">
                    <option value="${e.value.number}" <c:if test="${e.value.number == employee.number}">selected="selected"</c:if>>${e.value.fullName}</option>
                </c:forEach>
            </select>
            <fmt:formatDate var="weekString" value="${week}" pattern="yyyy-MM-dd"/>
            <input type="hidden" name="week" value="${weekString}"/>
            <input type="submit" value="Retrieve"/>
        </form>

        <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

        <h2>Timesheet for ${employee.fullName} for the week of ${thisWeekStart}</h2>

        <sql:query dataSource="jdbc/sarariman" var="entries">
            SELECT hours.task, hours.description, hours.date, hours.duration, tasks.name
            FROM hours
            INNER JOIN tasks ON hours.task=tasks.id
            WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY)
            ORDER BY hours.date DESC, hours.task ASC
            <sql:param value="${employee.number}"/>
            <sql:param value="${thisWeekStart}"/>
            <sql:param value="${thisWeekStart}"/>
        </sql:query>
        <c:set var="totalHoursWorked" value="0.0"/>
        <c:set var="totalPTO" value="0.0"/>
        <table class="altrows" id="timesheet">
            <tr><th>Date</th><th>Task</th><th>Task #</th><th>Duration</th><th>Description</th></tr>
            <c:forEach var="entry" items="${entries.rows}">
                <tr>
                    <fmt:formatDate var="entryDate" value="${entry.date}" pattern="E, MMM d"/>
                    <td class="date">${entryDate}</td>
                    <td>${fn:escapeXml(entry.name)}</td>
                    <td class="task">${entry.task}</td>

                    <!-- FIXME: This needs to look this up somewhere. -->
                    <c:if test="${entry.task == 5}">
                        <c:set var="totalPTO" value="${totalPTO + entry.duration}"/>
                    </c:if>

                    <td class="duration">${entry.duration}</td>
                    <c:set var="entryDescription" value="${entry.description}"/>
                    <c:if test="${sarariman:containsHTML(entryDescription)}">
                        <c:set var="entryDescription" value="${entryDescription}"/>
                    </c:if>
                    <td>${fn:escapeXml(entryDescription)}</td>
                    <c:set var="totalHoursWorked" value="${totalHoursWorked + entry.duration}"/>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="3">Total</td>
                <td class="duration">${totalHoursWorked}</td>
                <td></td>
            </tr>
            <tr>
                <td colspan="3">Total PTO</td>
                <td class="duration">${totalPTO}</td>
                <td></td>
            </tr>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
