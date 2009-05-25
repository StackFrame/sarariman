<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
    <c:set var="project" value="${sarariman:project(db, param.project)}"/>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Uninvoiced time for ${project.customer.name} - ${fn:escapeXml(project.name)}</title>
    </head>

    <body>
        <p><a href="./">Home</a></p>
        <h1>Uninvoiced time for ${project.customer.name} - ${fn:escapeXml(project.name)}</h1>

        <c:if test="${sarariman:isAdministrator(user) && !empty param.create}">
            <c:forEach items="${paramValues.addToInvoiceEmployee}" varStatus="varStatus">
                <c:set var="index" value="addToInvoice${varStatus.index}"/>
                <c:if test="${param[index]}">
                    <sql:transaction dataSource="jdbc/sarariman">
                        <sql:update>
                            INSERT INTO invoices (id, employee, task, date) VALUES(?, ?, ?, ?)
                            <sql:param value="${param.invoiceName}"/>
                            <sql:param value="${paramValues.addToInvoiceEmployee[varStatus.index]}"/>
                            <sql:param value="${paramValues.addToInvoiceTask[varStatus.index]}"/>
                            <sql:param value="${paramValues.addToInvoiceDate[varStatus.index]}"/>
                        </sql:update>
                    </sql:transaction>
                </c:if>
            </c:forEach>
            <p>Created <a href="invoice?invoice=${param.invoiceName}">invoice ${param.invoiceName}</a> with selected entries.</p>
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
        <form method="POST">
            <label for="invoiceName">Create Invoice:</label>
            <input type="text" size="8" name="invoiceName" id="invoiceName"/>
            <input type="submit" value="Create" name="create" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/>
            <table>
                <tr><th>Employee</th><th>Task</th><th>Date</th><th>Duration</th><th>Invoice</th></tr>
                <c:forEach var="row" items="${result.rows}" varStatus="varStatus">
                    <tr>
                        <td>${directory.employeeMap[row.employee].fullName}</td>
                        <td>${row.task}</td>
                        <td>${row.date}</td>
                        <td>${row.duration}</td>
                        <td><input type="checkbox" name="addToInvoice${varStatus.index}" value="true" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if>/></td>
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