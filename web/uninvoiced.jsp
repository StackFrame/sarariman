<%--
  Copyright (C) 2009-2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<fmt:parseNumber var="project_id" value="${param.project}"/>
<c:set var="project" value="${sarariman.projects[project_id]}"/>
<c:if test="${!sarariman:isCostManager(user, project)}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Uninvoiced time for ${fn:escapeXml(customer.name)} - ${fn:escapeXml(project.name)}</title>
        <script type="text/javascript" src="utilities.js"/>

        <!-- jQuery -->
        <link type="text/css" href="jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />	
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script>
            $(function() {
                $( "#pop_start" ).datepicker({dateFormat: 'yy-mm-dd'});
                $( "#pop_end" ).datepicker({dateFormat: 'yy-mm-dd'});
                $( "#filter_pop_start" ).datepicker({dateFormat: 'yy-mm-dd'});
                $( "#filter_pop_end" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body  onload="altRows()">
        <%@include file="header.jsp" %>

        <!-- FIXME: Add customer and project hyperlinks. -->
        <c:url var="customerLink" value="customer"><c:param name="id" value="${customer.id}"/></c:url>
        <c:url var="projectLink" value="project"><c:param name="id" value="${param.project}"/></c:url>
        <h1>Uninvoiced time for <a href="${customerLink}">${fn:escapeXml(customer.name)}</a> - <a href="${projectLink}">${fn:escapeXml(project.name)}</a></h1>

        <c:if test="${user.administrator && !empty param.create}">
            <c:set var="createdInvoice" value="${sarariman:createInvoice(sarariman, customer, project, param.pop_start,
                                                 param.pop_end, pageContext.request.parameterMap, paramValues.addToInvoiceEmployee,
                                                 paramValues.addToInvoiceTask, paramValues.addToInvoiceDate, paramValues.addToInvoiceService)}"/>
            <p>Created <a href="invoice?invoice=${createdInvoice.id}">invoice ${createdInvoice.id}</a> with selected entries.</p>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT h.employee, h.task, h.date, h.duration, t.name, t.project
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            LEFT OUTER JOIN invoices AS i ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            WHERE p.id = ? AND i.id IS NULL AND h.duration > 0 AND h.service_agreement IS NULL
            ORDER BY h.date ASC, h.employee ASC, h.task ASC
            <sql:param value="${param.project}"/>
        </sql:query>

        <sql:query dataSource="jdbc/sarariman" var="servicesResultSet">
            SELECT b.id, a.period_rate, b.pop_start, b.pop_end, a.description
            FROM billed_services as b
            JOIN service_agreements AS a ON b.service_agreement = a.id
            JOIN projects AS p ON a.project = p.id
            WHERE b.invoice IS NULL AND p.id = ?
            ORDER BY b.pop_start ASC
            <sql:param value="${param.project}"/>
        </sql:query>

        <c:choose>
            <c:when test="${result.rowCount > 0}">
                <fmt:parseDate var="pop_start" value="${result.rows[0].date}" pattern="yyyy-MM-dd"/>
                <c:set var="pop_start" value="${du:weekStart(pop_start)}"/>
            </c:when>
            <c:otherwise>
                <c:set var="pop_start" value="${du:weekStart(du:now())}"/>
            </c:otherwise>
        </c:choose>

        <c:set var="pop_end" value="${du:weekEnd(du:prevWeek(du:weekStart(du:now())))}"/>

        <!-- FIXME: Need task picker. -->
        <!-- FIXME: Add subtotal by task. -->

        <form method="GET">
            <label>Filter:</label><br/>
            <label for="filter_pop_start">Period of Performance Start: </label>
            <input size="10" type="text" id="filter_pop_start" name="filter_pop_start" value="${param.filter_pop_start}"/>

            <label for="filter_pop_end">End: </label>
            <input size="10" type="text" id="filter_pop_end" name="filter_pop_end" value="${param.filter_pop_end}"/><br/>

            <label for="task">Task: </label>
            <input type="text" id="task" name="filter_task" value="${param.filter_task}"/><br/>

            <input type="hidden" name="project" value="${param.project}"/>

            <input type="submit" value="Filter" name="filter"/><br/>
        </form>

        <form method="POST">
            <label>Create Invoice:</label><br/>

            <label for="pop_start">Period of Performance Start: </label>
            <input size="10" type="text" id="pop_start" name="pop_start" value="<fmt:formatDate value="${pop_start}" pattern="yyyy-MM-dd"/>"/>

            <label for="pop_end">End: </label>
            <input size="10" type="text" id="pop_end" name="pop_end" value="<fmt:formatDate value="${pop_end}" pattern="yyyy-MM-dd"/>"/><br/>

            <input type="submit" value="Create" name="create" <c:if test="${!user.administrator}">disabled="true"</c:if>/><br/>

            <table class="altrows">
                <caption>Services</caption>
                <thead>
                    <tr>
                        <th>Description</th>
                        <th>Start</th>
                        <th>End</th>
                        <th>Cost</th>
                        <th>Invoice</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${servicesResultSet.rows}" varStatus="varStatus">
                        <tr>
                            <td>${fn:escapeXml(row.description)}</td>
                            <td>${row.pop_start}</td>
                            <td>${row.pop_end}</td>
                            <td class="currency"><fmt:formatNumber type="currency" value="${row.period_rate}"/></td>
                            <td>
                                <input type="checkbox" name="addToInvoiceService" value="${row.id}" checked="true"
                                       <c:if test="${!user.administrator}">disabled="true"</c:if>
                                       />
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <br/>

            <table class="altrows">
                <caption>Labor</caption>
                <thead>
                    <tr><th rowspan="2">Employee</th><th colspan="2">Task</th><th rowspan="2">Date</th><th rowspan="2">Duration</th><th rowspan="2">Cost</th><th rowspan="2">Invoice</th></tr>
                    <tr><th>ID</th><th>Name</th></tr>
                </thead>
                <tbody>
                    <fmt:parseDate var="filter_pop_start" value="${param.filter_pop_start}" pattern="yyyy-MM-dd"/>
                    <fmt:parseDate var="filter_pop_end" value="${param.filter_pop_end}" pattern="yyyy-MM-dd"/>
                    <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
                    <c:set var="laborCategories" value="${sarariman.laborCategories}"/>
                    <c:set var="totalApproved" value="0.0"/>
                    <c:set var="totalRecorded" value="0.0"/>
                    <c:forEach var="row" items="${result.rows}" varStatus="varStatus">
                        <fmt:parseDate var="date" value="${row.date}" pattern="yyyy-MM-dd"/>
                        <c:if test="${(empty filter_pop_start || date ge filter_pop_start) && (empty filter_pop_end || date le filter_pop_end) && (empty param.filter_task || param.filter_task == row.task)}">
                            <tr>
                                <fmt:formatDate var="timesheetWeek" value="${du:weekStart(row.date)}" pattern="yyyy-MM-dd"/>
                                <c:url var="timesheet" value="timesheet">
                                    <c:param name="week" value="${timesheetWeek}"/>
                                    <c:param name="employee" value="${row.employee}"/>
                                </c:url>
                                <td>${directory.byNumber[row.employee].fullName}</td>
                                <td>${row.task}</td>
                                <td>${fn:escapeXml(row.name)}</td>
                                <td><a href="${fn:escapeXml(timesheet)}">${row.date}</a></td>
                                <td class="duration"><a href="${fn:escapeXml(timesheet)}">${row.duration}</a></td>
                                <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                                <c:set var="totalRecorded" value="${totalRecorded + costData.cost}"/> 
                                <td class="currency"><fmt:formatNumber type="currency" value="${costData.cost}"/></td>
                                <td>
                                    <input type="checkbox" name="addToInvoice${varStatus.index}" value="true"
                                           <c:if test="${date >= pop_start && date <= pop_end}">checked="true" <c:set var="totalApproved" value="${totalApproved + costData.cost}"/></c:if>
                                           <c:if test="${!user.administrator}">disabled="true"</c:if>
                                           />
                                </td>
                                <input type="hidden" name="addToInvoiceEmployee" value="${row.employee}"/>
                                <input type="hidden" name="addToInvoiceTask" value="${row.task}"/>
                                <input type="hidden" name="addToInvoiceDate" value="${row.date}"/>
                            </tr>
                        </c:if>
                    </c:forEach>
                    <!-- FIXME: Add total duration. -->
                    <tr><td colspan="5">Total Approved</td><td class="currency"><fmt:formatNumber type="currency" value="${totalApproved}"/></td><td></td></tr>
                    <tr><td colspan="5">Total Recorded</td><td class="currency"><fmt:formatNumber type="currency" value="${totalRecorded}"/></td><td></td></tr>
                </tbody>
            </table>
        </form>

        <%@include file="footer.jsp" %>
    </body>
</html>
