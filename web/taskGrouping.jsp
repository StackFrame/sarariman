<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:query dataSource="jdbc/sarariman" var="result">
        SELECT * FROM task_grouping where id=?
        <sql:param value="${param.id}"/>
    </sql:query>
    <c:set var="grouping" value="${result.rows[0]}"/>
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>${fn:escapeXml(grouping.name)}</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>${fn:escapeXml(grouping.name)}</h1>

        <h2>Period of Performance</h2>
        <p>Start: ${grouping.pop_start} End: ${grouping.pop_end}</p>

        <sql:query dataSource="jdbc/sarariman" var="employeesResult">
            SELECT employee FROM task_grouping_employee AS e JOIN task_grouping AS t ON t.id = e.grouping WHERE t.id=?
            <sql:param value="${param.id}"/>
        </sql:query>

        <h2>Employees</h2>
        <ul>
            <c:forEach var="row" items="${employeesResult.rows}">
                <li>${directory.byNumber[row.employee].fullName}</li>
            </c:forEach>
        </ul>

        <table id="taskGroupingElements">
            <tr><th>Task</th><th>Name</th><th>Fraction</th></tr>

            <sql:query dataSource="jdbc/sarariman" var="elementsResult">
                SELECT e.fraction, e.task, t.name FROM task_grouping_element AS e
                JOIN task_grouping AS g ON g.id = e.grouping
                JOIN tasks AS t ON t.id = e.task
                WHERE g.id = ?
                ORDER BY e.task
                <sql:param value="${param.id}"/>
            </sql:query>

            <c:set var="total" value="${0.0}"/>

            <c:forEach var="row" items="${elementsResult.rows}">
                <tr>
                    <td><a href="task?task_id=${row.task}">${row.task}</a></td>
                    <td><a href="task?task_id=${row.task}">${fn:escapeXml(row.name)}</a></td>
                    <td class="percentage"><fmt:formatNumber value="${row.fraction}" type="percent"/></td>
                </tr>
                <c:set var="total" value="${total + row.fraction}"/>
            </c:forEach>
        </table>

        <c:if test="${total > 1 || total < 1}"><%-- There has got to be a smarter way. --%>
            <p class="error">Fractions do not total 100%.</p>
        </c:if>

        <%@include file="footer.jsp" %>
    </body>
</html>
