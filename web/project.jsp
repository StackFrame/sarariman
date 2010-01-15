<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <fmt:parseNumber var="project_id" value="${param.id}"/>
    <c:set var="project" value="${sarariman.projects[project_id]}"/>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="utilities.js"/>
        <title>Project ${project.id}</title>
    </head>
    <body onload="altRows('tasks');altRows('categories');altRows('rates');altRows('task_assignments');">
        <%@include file="header.jsp" %>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT project FROM project_managers WHERE employee=? AND project=?
            <sql:param value="${user.number}"/>
            <sql:param value="${param.id}"/>
        </sql:query>
        <c:set var="isManager" value="${resultSet.rowCount == 1}"/>

        <h1>Project ${project.id}</h1>
        <form method="POST" action="projectController">
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="id" value="${project.id}"/>

            <label for="name">Name: </label>
            <input type="text" size="40" id="name" name="name" value="${fn:escapeXml(project.name)}"/><br/>

            <label for="customer">Customer: </label>
            <select id="customer" name="customer">
                <c:forEach var="entry" items="${sarariman.customers}">
                    <option value="${entry.key}" <c:if test="${entry.key == project.customer}">selected="selected"</c:if>>${fn:escapeXml(entry.value.name)}</option>
                </c:forEach>
            </select><br/>

            <label for="funded">Funded: </label>
            <input type="text" size="13" id="funded" name="funded" value="${fn:escapeXml(project.funded)}"/><br/>

            <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>

        <h2>Managers</h2>
        <ul>
            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT employee
                FROM project_managers
                WHERE project=?
                <sql:param value="${project.id}"/>
            </sql:query>
            <c:forEach var="row" items="${result.rows}">
                <c:url var="link" value="employee">
                    <c:param name="id" value="${row.employee}"/>
                </c:url>
                <li><a href="${link}">${directory.byNumber[row.employee].fullName}</a></li>
            </c:forEach>
        </ul>

        <table class="altrows" id="tasks">
            <caption>Tasks</caption>
            <tr><th>ID</th><th>Task</th><th>Active</th></tr>
            <c:forEach var="task" items="${project.tasks}">
                <tr>
                    <c:url var="link" value="task"><c:param name="task_id" value="${task.id}"/></c:url>
                    <td><a href="${link}">${task.id}</a></td>
                    <td><a href="${link}">${fn:escapeXml(task.name)}</a></td>
                    <td>
                        <form>
                            <input type="checkbox" name="active" disabled="true" <c:if test="${task.active}">checked="checked"</c:if>/>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT a.employee, t.id, t.name
            FROM task_assignments as a
            JOIN tasks AS t on t.id = a.task
            JOIN projects AS p on p.id = t.project
            WHERE p.id = ?
            <sql:param value="${project.id}"/>
        </sql:query>
        <table class="altrows" id="task_assignments">
            <caption>Task Assignments</caption>
            <tr><th>Employee</th><th colspan="2">Task</th></tr>
            <c:forEach var="row" items="${resultSet.rows}">
                <tr>
                    <c:url var="link" value="employee"><c:param name="id" value="${row.employee}"/></c:url>
                    <td><a href="${link}">${sarariman.directory.byNumber[row.employee].fullName}</a></td>
                    <c:url var="link" value="task"><c:param name="task_id" value="${task.id}"/></c:url>
                    <td><a href="${link}">${row.id}</a></td><td><a href="${link}">${fn:escapeXml(row.name)}</a></td>
                </tr>
            </c:forEach>
        </table>

        <c:if test="${isManager || user.administrator}">

            <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
            <c:set var="laborCategories" value="${sarariman.laborCategories}"/>

            <table class="altrows" id="categories">
                <caption>Labor Categories</caption>
                <tr><th>Labor Category</th><th>Rate</th><th>Start</th><th>End</th><th colspan="2">Expended</th></tr>
                <tr><th colspan="4"></th><th>Hours</th><th>Dollars</th></tr>
                <c:forEach var="entry" items="${sarariman.laborCategories}">
                    <c:if test="${entry.value.project == project.id}">
                        <c:set var="category" value="${entry.value}"/>
                        <c:url var="catLink" value="laborcategory"><c:param name="id" value="${entry.key}"/></c:url>
                        <tr>
                            <td><a href="${catLink}">${category.name}</a></td>
                            <td class="currency"><a href="${catLink}"><fmt:formatNumber type="currency" value="${category.rate}"/></a></td>
                            <td><a href="${catLink}">${category.periodOfPerformanceStart}</a></td>
                            <td><a href="${catLink}">${category.periodOfPerformanceEnd}</a></td>

                            <sql:query dataSource="jdbc/sarariman" var="result">
                                SELECT h.employee, h.date, h.duration, t.project
                                FROM hours AS h
                                JOIN tasks AS t on h.task = t.id
                                JOIN projects AS p ON t.project = p.id
                                WHERE p.id = ? AND t.billable = TRUE
                                <sql:param value="${project.id}"/>
                            </sql:query>
                            <c:set var="expendedCost" value="0"/>
                            <c:set var="expendedDuration" value="0"/>
                            <c:forEach var="row" items="${result.rows}">
                                <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                                <c:if test="${costData.laborCategory.id == category.id}">
                                    <c:set var="expendedDuration" value="${expendedDuration + row.duration}"/>
                                    <c:set var="expendedCost" value="${expendedCost + costData.cost}"/>
                                </c:if>
                            </c:forEach>
                            <td class="duration">${expendedDuration}</td>
                            <td class="currency"><fmt:formatNumber type="currency" value="${expendedCost}"/></td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>

            <table class="altrows" id="rates">
                <caption>Labor Category Assignments</caption>
                <tr><th>Employee</th><th>Labor Category</th><th>Start</th><th>End</th></tr>
                <c:forEach var="entry" items="${sarariman.projectBillRates}">
                    <c:set var="laborCategory" value="${sarariman.laborCategories[entry.laborCategory]}"/>
                    <c:if test="${laborCategory.project == project.id}">
                        <tr>
                            <c:url var="link" value="employee">
                                <c:param name="id" value="${entry.employee.number}"/>
                            </c:url>
                            <td><a href="${link}">${entry.employee.fullName}</a></td>
                            <td>${laborCategory.name}</td>
                            <td>${entry.periodOfPerformanceStart}</td>
                            <td>${entry.periodOfPerformanceEnd}</td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>

            <sql:query dataSource="jdbc/sarariman" var="invoices">
                SELECT DISTINCT i.id
                FROM invoices AS i
                JOIN hours AS h ON i.task = h.task AND i.employee = h.employee AND i.date = h.date
                JOIN tasks AS t on t.id = h.task
                JOIN projects AS p on p.id = t.project
                WHERE p.id = ?
                ORDER BY id DESC
                <sql:param value="${project.id}"/>
            </sql:query>
            <h1>Invoices</h1>
            <ul>
                <c:forEach var="invoice" items="${invoices.rows}">
                    <c:url var="link" value="invoice">
                        <c:param name="invoice" value="${invoice.id}"/>
                    </c:url>
                    <li><a href="${link}">${invoice.id}</a></li>
                </c:forEach>
            </ul>

            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT h.employee, h.date, h.duration, t.project
                FROM hours AS h
                JOIN tasks AS t on h.task = t.id
                JOIN projects AS p ON t.project = p.id
                WHERE p.id = ? AND t.billable = TRUE
                <sql:param value="${project.id}"/>
            </sql:query>
            <c:set var="expended" value="0"/>
            <c:forEach var="row" items="${result.rows}">
                <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                <c:if test="${empty costData.laborCategory}">
                    <c:set var="missingLaborCategory" value="true"/>
                    <p class="error">Labor category or billing rate missing for ${sarariman.directory.byNumber[row.employee].fullName}!</p>
                </c:if>
                <c:set var="expended" value="${expended + costData.cost}"/>
            </c:forEach>

            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT h.employee, h.date, h.duration, t.project
                FROM hours AS h
                JOIN invoices AS i ON i.task = h.task AND i.employee = h.employee AND i.date = h.date
                JOIN tasks AS t on h.task = t.id
                JOIN projects AS p ON t.project = p.id
                WHERE p.id = ? AND t.billable = TRUE
                <sql:param value="${project.id}"/>
            </sql:query>
            <c:set var="invoiced" value="0"/>
            <c:forEach var="row" items="${result.rows}">
                <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                <c:set var="invoiced" value="${invoiced + costData.cost}"/>
            </c:forEach>

            <p>Invoiced: <fmt:formatNumber type="currency" value="${invoiced}"/><br/>
                Expended: <fmt:formatNumber type="currency" value="${expended}"/><br/>
                Remaining: <fmt:formatNumber type="currency" value="${project.funded - expended}"/></p>

            <c:if test="${missingLaborCategory}">
                <p class="error">There are labor categories missing from this project which are causing them to be excluded from expended amount!</p>
            </c:if>

        </c:if>

        <p>
            <c:url var="hoursByProject" value="hoursByProject">
                <c:param name="project" value="${param.id}"/>
            </c:url>
            <a href="${hoursByProject}">Hours billed to this project</a>.
        </p>

        <%@include file="footer.jsp" %>
    </body>
</html>
