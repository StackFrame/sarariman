<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <c:set var="employee" value="${directory.byNumber[param.id]}"/>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>${employee.fullName}</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>${employee.fullName}</h1>

        <c:if test="${user.administrator}">
            <h2>Direct Rate</h2>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT rate, effective
                FROM direct_rate
                WHERE employee=?
                ORDER BY effective DESC
                <sql:param value="${param.id}"/>
            </sql:query>
            <table>
                <tr><th>Rate</th><th>Effective Date</th></tr>
                <c:forEach var="rate_row" items="${resultSet.rows}">
                    <tr>
                        <td class="currency"><fmt:formatNumber type="currency" value="${rate_row.rate}"/></td>
                        <td>${rate_row.effective}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT project FROM project_managers WHERE employee=?
            <sql:param value="${param.id}"/>
        </sql:query>
        <c:if test="${resultSet.rowCount != 0}">
            <h2>Projects Managed</h2>
            <ul>
                <c:forEach var="mapping_row" items="${resultSet.rows}">
                    <c:set var="project" value="${sarariman.projects[mapping_row.project]}"/>
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                    <c:url var="link" value="project">
                        <c:param name="id" value="${mapping_row.project}"/>
                    </c:url>
                    <li><a href="${link}">${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</a></li>
                </c:forEach>
            </ul>
        </c:if>

        <h2>Task Assignments</h2>
        <ul>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT a.task, t.name, t.project
                FROM task_assignments AS a
                JOIN tasks AS t ON t.id = a.task
                WHERE a.employee=?
                <sql:param value="${param.id}"/>
            </sql:query>
            <c:forEach var="mapping_row" items="${resultSet.rows}">
                <c:url var="link" value="task">
                    <c:param name="task_id" value="${mapping_row.task}"/>
                </c:url>
                <c:if test="${!empty mapping_row.project}">
                    <c:set var="project" value="${sarariman.projects[mapping_row.project]}"/>
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                </c:if>
                <li><a href="${link}">${fn:escapeXml(mapping_row.name)} (${mapping_row.task})
                        <c:if test="${!empty mapping_row.project}">
                            - ${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}
                        </c:if>
                    </a>
                </li>
            </c:forEach>
        </ul>

        <h2>Tasks Worked</h2>
        <ul>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT DISTINCT(h.task), t.name, t.project
                FROM hours AS h
                JOIN tasks AS t ON t.id = h.task
                WHERE h.employee=?
                <sql:param value="${param.id}"/>
            </sql:query>
            <c:forEach var="mapping_row" items="${resultSet.rows}">
                <c:url var="link" value="task">
                    <c:param name="task_id" value="${mapping_row.task}"/>
                </c:url>
                <c:if test="${!empty mapping_row.project}">
                    <c:set var="project" value="${sarariman.projects[mapping_row.project]}"/>
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                </c:if>
                <li><a href="${link}">${fn:escapeXml(mapping_row.name)} (${mapping_row.task})
                        <c:if test="${!empty mapping_row.project}">
                            - ${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}
                        </c:if>
                    </a>
                </li>
            </c:forEach>
        </ul>

        <%@include file="footer.jsp" %>
    </body>
</html>
