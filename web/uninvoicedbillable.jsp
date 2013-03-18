<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Uninvoiced Billable</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Uninvoiced Billable</h1>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT DISTINCT h.employee, h.task, h.date, t.name
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            LEFT OUTER JOIN invoices AS i ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            WHERE t.billable = 1 AND i.id IS NULL AND h.duration > 0
            ORDER BY h.date ASC
        </sql:query>
        <table>
            <tr><th>Employee</th><th>Date</th><th>Task</th></tr>
            <c:forEach var="row" items="${result.rows}" varStatus="varStatus">
                <tr>
                    <td>${sarariman.directory.byNumber[row.employee].fullName}</td>
                    <td>
                        <c:url var="entryLink" value="editentry">
                            <c:param name="task" value="${row.task}"/>
                            <c:param name="date" value="${row.date}"/>
                            <c:param name="employee" value="${row.employee}"/>
                        </c:url>
                        <a href="${fn:escapeXml(entryLink)}">${row.date}</a>
                    </td>
                    <td>
                        <a href="${sarariman.tasks.map[row.task].URL}">${fn:escapeXml(row.name)}</a>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
