<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<sql:setDataSource dataSource="jdbc/sarariman" var="db"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <!-- Grrr.  FIXME: I would like to do a single join here with column name aliases, but some bug in JDBC prevents it. -->
    <sql:query dataSource="${db}" var="project" >
        SELECT name
        FROM projects
        WHERE id=?
        <sql:param value="${param.project}"/>
    </sql:query>
    <sql:query dataSource="${db}" var="customer" >
        SELECT c.name
        FROM customers AS c
        JOIN projects AS p ON p.customer = c.id
        WHERE p.id=?
        <sql:param value="${param.project}"/>
    </sql:query>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>${customer.rows[0].name} - ${fn:escapeXml(project.rows[0].name)} - ${param.week}</title>
    </head>

    <body>
        <h1>${customer.rows[0].name} - ${fn:escapeXml(project.rows[0].name)} - ${param.week}</h1>

        <sql:query dataSource="${db}" var="result">
            SELECT DISTINCT h.employee
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            WHERE p.id = ? AND h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY)
            ORDER BY h.employee ASC
            <sql:param value="${param.project}"/>
            <sql:param value="${param.week}"/>
            <sql:param value="${param.week}"/>
        </sql:query>
        <ul>
            <c:forEach var="row" items="${result.rows}">
                <c:url var="target" value="timereport.jsp">
                    <c:param name="project" value="${param.project}"/>
                    <c:param name="week" value="${param.week}"/>
                    <c:param name="employee" value="${row.employee}"/>
                </c:url>
                <li><a href="${fn:escapeXml(target)}">${directory.employeeMap[row.employee].fullName}</a></li>
            </c:forEach>
        </ul>
        <%@include file="footer.jsp" %>
    </body>
</html>