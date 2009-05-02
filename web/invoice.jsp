<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<sql:setDataSource dataSource="jdbc/sarariman" var="db"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Invoice ${param.invoice}</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>
        <h1>Invoice ${param.invoice}</h1>
        <sql:query dataSource="${db}" var="result">
            SELECT i.employee, i.task, i.date, h.duration
            FROM invoices AS i
            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            WHERE i.id = ?
            ORDER BY h.date ASC, h.employee ASC, h.task ASC
            <sql:param value="${param.invoice}"/>
        </sql:query>
        <table>
            <tr>
                <th>Employee</th>
                <th>Task</th>
                <th>Date</th>
                <th>Duration</th>
            </tr>
            <c:forEach var="row" items="${result.rows}">
                <tr>
                    <td>${directory.employeeMap[row.employee].fullName}</td>
                    <td>${row.task}</td>
                    <td>${row.date}</td>
                    <td>${row.duration}</td>
                </tr>
            </c:forEach>
            <sql:query dataSource="${db}" var="sum">
                SELECT SUM(h.duration) AS total
                FROM invoices AS i
                JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                WHERE i.id = ?
                <sql:param value="${param.invoice}"/>
            </sql:query>
            <tr><td>Total</td><td></td><td></td><td>${sum.rows[0].total}</td></tr>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>