<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        <title>Invoice ${param.invoice}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('hours')">
        <p><a href="./">Home</a></p>

        <h1>Invoice ${param.invoice}</h1>

        <c:if test="${user.administrator}">
            <form action="invoiceController" method="post">
                <input type="hidden" name="id" value="${param.invoice}"/>
                <input type="submit" name="action" value="delete"/>
            </form>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT i.employee, i.task, i.date, h.duration, t.project
            FROM invoices AS i
            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            JOIN tasks AS t on i.task = t.id
            JOIN projects AS p ON t.project = p.id
            WHERE i.id = ?
            ORDER BY h.date ASC, h.employee ASC, h.task ASC
            <sql:param value="${param.invoice}"/>
        </sql:query>
        <table class="altrows" id="hours">
            <caption>Entries</caption>
            <tbody>
                <tr>
                    <th>Employee</th>
                    <th>Task</th>
                    <th>Labor Category</th>
                    <th>Date</th>
                    <th>Rate</th>
                    <th>Duration</th>
                    <th>Cost</th>
                </tr>
                <c:set var="totalCost" value="0"/>
                <c:forEach var="row" items="${result.rows}">
                    <c:set var="costData" value="${sarariman:cost(sarariman, row.project, row.employee, row.date, row.duration)}"/>
                    <c:set var="totalCost" value="${totalCost + costData.cost}"/>
                    <tr>
                        <td>${directory.byNumber[row.employee].fullName}</td>
                        <td><a href="task?task_id=${row.task}">${row.task}</a></td>
                        <c:choose>
                            <c:when test="${empty costData.laborCategory}">
                                <td class="error">no labor category</td>                                                        
                            </c:when>
                            <c:otherwise>
                                <td>${costData.laborCategory}</td>                                                        
                            </c:otherwise>
                        </c:choose>
                        <td>${row.date}</td>
                        <c:choose>
                            <c:when test="${empty costData.laborCategory}">
                                <td class="error">no rate</td>
                            </c:when>
                            <c:otherwise>
                                <td class="currency"><fmt:formatNumber type="currency" value="${costData.rate}"/></td>
                            </c:otherwise>
                        </c:choose>
                        <td class="duration">${row.duration}</td>
                        <c:choose>
                            <c:when test="${empty costData.laborCategory}">
                                <td class="error">no rate</td>
                            </c:when>
                            <c:otherwise>
                                <td class="currency"><fmt:formatNumber type="currency" value="${costData.cost}"/></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
                <sql:query dataSource="jdbc/sarariman" var="sum">
                    SELECT SUM(h.duration) AS total
                    FROM invoices AS i
                    JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                    WHERE i.id = ?
                    <sql:param value="${param.invoice}"/>
                </sql:query>
                <tr><td>Total</td><td></td><td></td><td></td><td></td><td class="duration">${sum.rows[0].total}</td><td class="currency"><fmt:formatNumber type="currency" value="${totalCost}"/></td></tr>
            </tbody>
        </table>

        <p><a href="laborcosts.csv?id=${param.invoice}">Download as CSV</a></p>

        <sql:query dataSource="jdbc/sarariman" var="employees">
            SELECT DISTINCT h.employee
            FROM invoices AS i
            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            WHERE i.id = ?
            <sql:param value="${param.invoice}"/>
        </sql:query>

        <table>
            <caption>Total by Employee and Task</caption>
            <tbody>
                <tr><th>Employee</th><th>Task</th><th>Total</th></tr>
                <c:forEach var="employeeRows" items="${employees.rows}">
                    <sql:query dataSource="jdbc/sarariman" var="tasks">
                        SELECT DISTINCT h.task
                        FROM invoices AS i
                        JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                        WHERE i.id = ? AND h.employee = ?
                        <sql:param value="${param.invoice}"/>
                        <sql:param value="${employeeRows.employee}"/>
                    </sql:query>
                    <c:forEach var="taskRows" items="${tasks.rows}">
                        <tr>
                            <td>${directory.byNumber[employeeRows.employee].fullName}</td>
                            <td>${taskRows.task}</td>
                            <sql:query dataSource="jdbc/sarariman" var="totals">
                                SELECT SUM(h.duration) AS total
                                FROM invoices AS i
                                JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                                WHERE i.id = ? AND h.employee = ? AND h.task = ?
                                <sql:param value="${param.invoice}"/>
                                <sql:param value="${employeeRows.employee}"/>
                                <sql:param value="${taskRows.task}"/>
                            </sql:query>
                            <td class="duration">${totals.rows[0].total}</td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>

        <table>
            <caption>Total by Employee</caption>
            <tbody>
                <tr><th>Employee</th><th>Total</th></tr>
                <c:forEach var="employeeRows" items="${employees.rows}">
                    <tr>
                        <td>${directory.byNumber[employeeRows.employee].fullName}</td>
                        <sql:query dataSource="jdbc/sarariman" var="totals">
                            SELECT SUM(h.duration) AS total
                            FROM invoices AS i
                            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                            WHERE i.id = ? AND h.employee = ?
                            <sql:param value="${param.invoice}"/>
                            <sql:param value="${employeeRows.employee}"/>
                        </sql:query>
                        <td class="duration">${totals.rows[0].total}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
