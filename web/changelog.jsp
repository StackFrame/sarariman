<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Changelog</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <c:set var="start" value="${param.start}"/>
        <c:if test="${empty start}">
            <c:set var="start" value="${du:weekStart(du:now())}"/>
            <fmt:formatDate var="start" value="${start}" type="date" pattern="yyyy-MM-dd"/>
        </c:if>
        <c:set var="end" value="${param.end}"/>
        <c:if test="${empty end}">
            <c:set var="end" value="${du:nextWeek(du:now())}"/>
            <fmt:formatDate var="end" value="${end}" type="date" pattern="yyyy-MM-dd"/>
        </c:if>
        <c:set var="maxRows" value="${param.maxRows}"/>
        <c:if test="${empty maxRows}">
            <c:set var="maxRows" value="50"/>
        </c:if>
        <fmt:parseNumber var="maxRows" value="${maxRows}"/>
        
        <form method="get">
            <label for="start">Start: </label>
            <input type="text" name="start" id="start" size="10" value="${start}"/>
            <label for="end">End: </label>
            <input type="text" name="end" id="end" size="10" value="${end}"/>
            <label for="maxRows">Maximum Number of Rows: </label>
            <input type="text" name="maxRows" id="end" size="3" value="${maxRows}"/>
            <input type="submit" value="Search"/>
        </form>

        <sql:query dataSource="jdbc/sarariman" var="entries">
            SELECT * FROM hours_changelog WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC LIMIT ?
            <sql:param value="${start}"/>
            <sql:param value="${end}"/>
            <sql:param value="${maxRows}"/>
        </sql:query>
        <table id="entries">
            <tr><th>Timestamp</th><th>Date</th><th>Task #</th><th>Duration</th><th>Employee</th><th>Remote Address</th><th>Remote User</th><th>Reason</th><th></th></tr>
            <c:forEach var="entry" items="${entries.rows}">
                <tr>
                    <td>${entry.timestamp}</td>
                    <td>${entry.date}</td>
                    <td>${entry.task}</td>
                    <td>${entry.duration}</td>
                    <td>${directory.byNumber[entry.employee].userName}</td>
                    <td>${entry.remote_address}</td>
                    <td>${directory.byNumber[entry.remote_user].userName}</td>
                    <td>${entry.reason}</td>
                    <td>
                        <c:url var="entryLink" value="editentry">
                            <c:param name="task" value="${entry.task}"/>
                            <c:param name="date" value="${entry.date}"/>
                            <c:param name="employee" value="${entry.employee}"/>
                        </c:url>
                        <a href="${fn:escapeXml(entryLink)}">Entry</a>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
