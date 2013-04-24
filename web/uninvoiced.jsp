<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<fmt:parseNumber var="project_id" value="${param.project}"/>
<c:set var="project" value="${sarariman.projects.map[project_id]}"/>
<c:if test="${!sarariman:isCostManager(user, project)}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Uninvoiced time for ${fn:escapeXml(project.client.name)} - ${fn:escapeXml(project.name)}</title>

        <!-- jQuery -->
        <link type="text/css" href="jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />	
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
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <!-- FIXME: Add customer and project hyperlinks. -->
            <c:url var="customerLink" value="customer"><c:param name="id" value="${project.client.id}"/></c:url>
            <c:url var="projectLink" value="project"><c:param name="id" value="${param.project}"/></c:url>
            <h1>Uninvoiced time for <a href="${customerLink}">${fn:escapeXml(project.client.name)}</a> - <a href="${projectLink}">${fn:escapeXml(project.name)}</a></h1>

            <c:if test="${user.administrator && !empty param.create}">
                <c:set var="createdInvoice" value="${sarariman:createInvoice(sarariman, project.client, project, param.pop_start,
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

            <c:set var="pop_end" value="${du:week(du:now()).previous.end.time}"/>

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

                    <table>
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
                        <c:forEach var="row" items="${servicesResultSet.rows}">
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

                <jsp:useBean id="taskTotals" class="java.util.HashMap" scope="page"/>

                <table>
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
                        <c:set var="elementIndex" value="0"/>
                        <c:forEach var="row" items="${result.rows}">
                            <fmt:parseDate var="date" value="${row.date}" pattern="yyyy-MM-dd"/>
                            <c:if test="${(empty filter_pop_start || date ge filter_pop_start) && (empty filter_pop_end || date le filter_pop_end) && (empty param.filter_task || param.filter_task == row.task)}">
                                <tr>
                                    <fmt:formatDate var="timesheetWeek" value="${du:weekStart(row.date)}" pattern="yyyy-MM-dd"/>
                                    <c:url var="timesheet" value="timesheet">
                                        <c:param name="week" value="${timesheetWeek}"/>
                                        <c:param name="employee" value="${row.employee}"/>
                                    </c:url>
                                    <td>${directory.byNumber[row.employee].fullName}</td>
                                    <c:set var="task" value="${sarariman.tasks.map[row.task]}"/>
                                    <td>${row.task}</td>
                                    <td>${fn:escapeXml(row.name)}</td>
                                    <td><a href="${fn:escapeXml(timesheet)}">${row.date}</a></td>
                                    <td class="duration"><a href="${fn:escapeXml(timesheet)}">${row.duration}</a></td>
                                    <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.task, row.date, row.duration)}"/>
                                    <c:set var="cost" value="${costData.cost}"/>
                                    <c:set var="totalRecorded" value="${totalRecorded + costData.cost}"/> 
                                    <td class="currency"><fmt:formatNumber type="currency" value="${costData.cost}"/></td>
                                    <td>
                                        <input type="checkbox" name="addToInvoice${elementIndex}" value="true"
                                               <c:if test="${date >= pop_start && date <= pop_end}">checked="true" <c:set var="totalApproved" value="${totalApproved + costData.cost}"/></c:if>
                                               <c:if test="${!user.administrator}">disabled="true"</c:if>
                                                   />
                                        </td>
                                <input type="hidden" name="addToInvoiceEmployee" value="${row.employee}"/>
                            <input type="hidden" name="addToInvoiceTask" value="${row.task}"/>
                            <input type="hidden" name="addToInvoiceDate" value="${row.date}"/>
                            <%
                                Object task = pageContext.getAttribute("task");
                                java.math.BigDecimal cost = (java.math.BigDecimal)pageContext.getAttribute("cost");
                                java.math.BigDecimal old = (java.math.BigDecimal)taskTotals.get(task);
                                if (old == null) {
                                    taskTotals.put(task, cost);
                                } else {
                                    taskTotals.put(task, old.add(cost));
                                }
                            %>
                            </tr>
                            <c:set var="elementIndex" value="${elementIndex + 1}"/>
                        </c:if>
                    </c:forEach>
                    <!-- FIXME: Add total duration. -->
                    <tr><td colspan="5">Total Approved</td><td class="currency"><fmt:formatNumber type="currency" value="${totalApproved}"/></td><td></td></tr>
                    <tr><td colspan="5">Total Recorded</td><td class="currency"><fmt:formatNumber type="currency" value="${totalRecorded}"/></td><td></td></tr>
                    </tbody>
                </table>

                <table>
                    <caption>Total by Task</caption>
                    <thead>
                        <tr>
                            <th colspan="2">Task</th>
                            <th rowspan="2">Total</th>
                        </tr>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- FIXME: Sort by task ID? -->
                        <c:forEach var="row" items="${taskTotals}">
                            <tr>
                                <td>${row.key.id}</td>
                                <td>${fn:escapeXml(row.key.name)}</td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${row.value}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

            </form>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
