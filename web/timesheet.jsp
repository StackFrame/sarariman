<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<c:set var="employeeNumber" value="${user.number}"/>
<sql:setDataSource dataSource="jdbc/sarariman" var="db"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Timesheet</title>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->
    <body>
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
            <label for="employee">Employee: </label>
            <select name="employee" id="employee">
                <c:forEach var="e" items="${directory.employees}">
                    <option value="${e.number}">${e.fullName}</option>
                </c:forEach>
            </select>
            <input type="submit" value="Retrieve"/>
        </form>

        <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

        <h2>Timesheet for ${directory.employeeMap[param.employee].fullName} for the week of ${thisWeekStart}</h2>

        <!-- FIXME: Can I do the nextWeek part in SQL? -->
        <sql:query dataSource="${db}" var="entries">
            SELECT hours.task, hours.description, hours.date, hours.duration, tasks.name FROM hours INNER JOIN tasks ON hours.task=tasks.id WHERE employee=? AND hours.date >= ? AND hours.date < ? ORDER BY hours.date DESC, hours.task ASC
            <sql:param value="${param.employee}"/>
            <sql:param value="${week}"/>
            <sql:param value="${du:nextWeek(week)}"/>
        </sql:query>
        <c:set var="totalHoursWorked" value="0.0"/>
        <table>
            <tr><th>Date</th><th>Task</th><th>Task #</th><th>Duration</th><th>Description</th></tr>
            <c:forEach var="entry" items="${entries.rows}">
                <tr>
                    <td>${entry.date}</td>
                    <td>${fn:escapeXml(entry.name)}</td>
                    <td>${entry.task}</td>
                    <td>${entry.duration}</td>
                    <c:set var="entryDescription" value="${entry.description}"/>
                    <c:if test="${sarariman:containsHTML(entryDescription)}">
                        <c:set var="entryDescription" value="${fn:escapeXml(entryDescription)}"/>
                    </c:if>
                    <td>${entryDescription}</td>
                    <c:set var="totalHoursWorked" value="${totalHoursWorked + entry.duration}"/>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="3">Total</td>
                <td>${totalHoursWorked}</td>
            </tr>
        </table>
    </body>
</html>