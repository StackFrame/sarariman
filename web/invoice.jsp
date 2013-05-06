<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<sql:query dataSource="jdbc/sarariman" var="invoice_info_result">
    SELECT i.project, i.sent, i.customer, i.payment_received, i.pop_start, i.pop_end, p.terms, DATE_ADD(i.sent, INTERVAL p.terms DAY) AS due
    FROM invoice_info AS i
    LEFT JOIN projects AS p ON i.project = p.id
    WHERE i.id = ?
    <sql:param value="${param.invoice}"/>
</sql:query>
<c:set var="invoice_info" value="${invoice_info_result.rows[0]}"/>
<c:set var="project" value="${sarariman.projects.map[invoice_info.project]}"/>

<sql:query dataSource="jdbc/sarariman" var="resultSet">
    SELECT project FROM project_managers WHERE employee=? AND project=?
    <sql:param value="${user.number}"/>
    <sql:param value="${invoice_info.project}"/>
</sql:query>
<c:set var="isProjectManager" value="${resultSet.rowCount != 0}" scope="request"/>

<c:set var="isCostManager" value="${sarariman:isCostManager(user, project)}" scope="request"/>

<c:if test="${!(user.administrator || user.invoiceManager || isProjectManager || isCostManager)}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <style type="text/css">
            @media screen, print {
                #returnAddress {
                    text-align: center;
                }
            }

            @media print {
                a {
                    text-decoration: none;
                }

                a[href]:after {
                    content: none;
                }

                table {
                    -fs-table-paginate: paginate;
                    page-break-inside: avoid;
                }

                @page {
                    size: letter;
                    @top-left { content: "Invoice ${param.invoice}";
                }

                @top-right {
                    content: "Page " counter(page) " of " counter(pages);
                }
            }
        </style>
        <title>Invoice ${param.invoice}</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <jsp:useBean id="documentNames" class="java.util.ArrayList" scope="request"/>
            <jsp:useBean id="documentLinks" class="java.util.ArrayList" scope="request"/>

            <div id="returnAddress" class="pull-right">
                <img alt="logo" src="${sarariman.logoURL}" height="86" width="182"/><br/><br/>
                StackFrame, LLC<br/>
                PO Box 2606<br/>
                Sanford, FL 32772-2606<br/>
                <strong>T</strong> 407-454-9193<br/>
                <strong>F</strong> 321-445-6045
            </div>

            <h1>Invoice</h1>

            <c:set var="customer" value="${sarariman.clients.map[invoice_info.customer]}"/>
            <fmt:formatDate var="sent" value="${invoice_info.sent}"/>
            <fmt:formatDate var="due" value="${invoice_info.due}"/>
            <fmt:formatDate var="received" value="${invoice_info.payment_received}"/>

            <p>
                <sql:query dataSource="jdbc/sarariman" var="emailResult">
                    SELECT c.name, c.title, c.id
                    FROM project_invoice_contacts as ptc
                    JOIN projects AS p ON ptc.project = p.id
                    JOIN contacts AS c ON c.id = ptc.contact
                    WHERE p.id = ?
                    <sql:param value="${project.id}"/>
                </sql:query>
                <c:forEach var="row" items="${emailResult.rows}">
                    <c:url var="contactLink" value="contact">
                        <c:param name="id" value="${row.id}"/>
                    </c:url>
                    Attn: <a href="${contactLink}" title="go to contact details for ${row.name}">${row.name}</a><c:if test="${!empty row.title}">, ${row.title}</c:if><br/>
                </c:forEach>

                <c:url var="customerLink" value="customer">
                    <c:param name="id" value="${customer.id}"/>
                </c:url>
                Customer: <a href="${customerLink}">${fn:escapeXml(customer.name)}</a><br/>
                Invoice: ${param.invoice}<br/>
                Date: ${sent}<br/>
                <c:url var="projectLink" value="project">
                    <c:param name="id" value="${project.id}"/>
                </c:url>
                Project: <a href="${projectLink}">${fn:escapeXml(project.name)}</a><br/>
                Period invoiced: ${invoice_info.pop_start} - ${invoice_info.pop_end}<br/>
                Terms: net ${invoice_info.terms} days<br/>
                <c:if test="${!empty project.contract}">
                    Contract: ${project.contract}<br/>
                </c:if>
                <c:if test="${!empty project.subcontract}">
                    Subcontract: ${project.subcontract}<br/>
                </c:if>
            </p>

            <div id="tracking" class="hidden-print">
                <p>Due: ${due}</p>
                <p>Payment received: ${received}</p>
                <p>Description: ${fn:escapeXml(invoice_info.description)}</p>
                <p>Comments: ${fn:escapeXml(invoice_info.comments)}</p>
            </div>

            <%
                documentNames.add("Invoice-" + request.getParameter("invoice") + ".pdf");
                documentLinks.add(String.format("invoice?outputType=pdf&invoice=%s", request.getParameter("invoice")));
            %>

            <div id="timesheets" class="hidden-print">
                <h2>Timesheets</h2>
                <ul>
                    <c:forEach var="week" items="${du:weekStartsRange(invoice_info.pop_start, invoice_info.pop_end)}">
                        <fmt:formatDate var="formattedWeek" value="${week}" pattern="yyyy-MM-dd"/>
                        <sql:query dataSource="jdbc/sarariman" var="timesheetResult">
                            SELECT DISTINCT h.employee
                            FROM hours as h
                            JOIN tasks AS t ON h.task = t.id
                            JOIN projects AS p ON t.project = p.id
                            WHERE p.id = ? AND h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY) AND h.duration > 0
                            ORDER BY h.employee ASC
                            <sql:param value="${project.id}"/>
                            <sql:param value="${week}"/>
                            <sql:param value="${week}"/>
                        </sql:query>
                        <c:forEach var="row" items="${timesheetResult.rows}">
                            <c:url var="html" value="timereport">
                                <c:param name="project" value="${project.id}"/>
                                <c:param name="week" value="${formattedWeek}"/>
                                <c:param name="employee" value="${row.employee}"/>
                            </c:url>
                            <c:url var="pdf" value="${html}">
                                <c:param name="outputType" value="pdf"/>
                                <c:param name="preferredFilename"
                                         value="Timesheet ${directory.byNumber[row.employee].fullName} ${formattedWeek}.pdf"/>
                            </c:url>
                            <li><a href="${fn:escapeXml(html)}">${directory.byNumber[row.employee].fullName} ${formattedWeek}</a>
                                <a href="${fn:escapeXml(pdf)}">[PDF]</a>
                                <c:set var="documentName" value="Timesheet ${directory.byNumber[row.employee].fullName} ${formattedWeek}.pdf"/>
                                <c:set var="documentLink" value="${pdf}"/>
                                <%
                                    documentNames.add(pageContext.getAttribute("documentName"));
                                    documentLinks.add(pageContext.getAttribute("documentLink"));
                                %>
                            </li>
                        </c:forEach>
                    </c:forEach>
                </ul>
            </div>

            <c:set var="entriesTableEmitted" value="${false}" scope="request"/>

            <c:forEach var="extension" items="${sarariman.extensions}">
                <jsp:include page="${extension.invoiceInclude}">
                    <jsp:param name="invoice" value="${param.invoice}"/>
                </jsp:include>
            </c:forEach>

            <c:if test="${empty laborCSVLink && !empty timesheetResult.rows}">
                <c:set var="laborCSVLink" value="laborcosts.csv?id=${param.invoice}" scope="request"/>
                <%
                    documentNames.add(String.format("laborcosts-%s.csv", request.getParameter("invoice")));
                    documentLinks.add(String.format("laborcosts.csv?id=%s", request.getParameter("invoice")));
                %>
            </c:if>

            <c:set var="errorsOccurred" value="false"/>

            <c:if test="${!entriesTableEmitted}">
                <sql:query dataSource="jdbc/sarariman" var="result">
                    SELECT i.employee, i.task, i.date, h.duration, t.project, t.line_item, t.name
                    FROM invoices AS i
                    JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                    JOIN tasks AS t on i.task = t.id
                    JOIN projects AS p ON t.project = p.id
                    WHERE i.id = ?
                    ORDER BY h.date ASC, h.employee ASC, h.task ASC
                    <sql:param value="${param.invoice}"/>
                </sql:query>

                <c:set var="invoiceTotal" value="0" scope="request"/>
                <c:set var="laborTotal" value="0" scope="request"/>
                <c:set var="expensesTotal" value="0" scope="request"/>
                <c:set var="servicesTotal" value="0" scope="request"/>
                <c:set var="odc_fee" value="0" scope="request"/>
                <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
                <c:set var="laborCategories" value="${sarariman.laborCategories}"/>

                <c:forEach var="row" items="${result.rows}">
                    <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.task, row.date, row.duration)}"/>
                    <c:set var="laborTotal" value="${laborTotal + costData.cost}" scope="request"/>
                </c:forEach>

                <sql:query dataSource="jdbc/sarariman" var="expenseResultSet">
                    SELECT e.employee, e.date, e.cost, e.description, e.task
                    FROM expenses AS e
                    JOIN tasks AS t on t.id = e.task
                    WHERE e.invoice = ?
                    ORDER BY e.employee ASC, e.date ASC, e.task ASC
                    <sql:param value="${param.invoice}"/>
                </sql:query>

                <c:forEach var="row" items="${expenseResultSet.rows}">
                    <c:set var="expensesTotal" value="${expensesTotal + row.cost}" scope="request"/>
                </c:forEach>

                <c:if test="${project.ODCFee > 0}">
                    <!-- FIXME: Need to do this with proper rounding. -->
                    <c:set var="odc_fee" value="${expensesTotal * project.ODCFee}"/>
                </c:if>

                <sql:query dataSource="jdbc/sarariman" var="servicesResultSet">
                    SELECT a.period_rate, b.pop_start, b.pop_end, a.description
                    FROM billed_services as b
                    JOIN service_agreements AS a ON b.service_agreement = a.id
                    WHERE b.invoice = ?
                    ORDER BY b.pop_start ASC
                    <sql:param value="${param.invoice}"/>
                </sql:query>

                <c:forEach var="row" items="${servicesResultSet.rows}">
                    <c:set var="servicesTotal" value="${servicesTotal + row.period_rate}" scope="request"/>
                </c:forEach>

                <c:set var="invoiceTotal" value="${expensesTotal + odc_fee + laborTotal + servicesTotal}" scope="request"/>

                <c:if test="${!empty project.invoiceText}">
                    <div>${project.invoiceText}</div>
                </c:if>

                <p>Total this invoice: <fmt:formatNumber type="currency" value="${invoiceTotal}"/><br/>
                    <c:if test="${project.funded > 0}">
                        Funded: <fmt:formatNumber type="currency" value="${project.funded}"/><br/>

                        <sql:query dataSource="jdbc/sarariman" var="previouslyBilledResult">
                            SELECT SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                            FROM hours AS h
                            JOIN invoices AS i ON i.task = h.task AND i.employee = h.employee AND i.date = h.date
                            JOIN tasks AS t on h.task = t.id
                            JOIN projects AS p on p.id = t.project
                            JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                            JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                            WHERE p.id = ? AND h.duration > 0 AND i.date < ?
                            <sql:param value="${project.id}"/>
                            <sql:param value="${invoice_info.pop_start}"/>
                        </sql:query>

                        <sql:query dataSource="jdbc/sarariman" var="previouslyInvoicedExpenseResult">
                            SELECT SUM(e.cost) AS costTotal
                            FROM expenses AS e
                            JOIN tasks AS t on t.id = e.task
                            JOIN projects AS p ON t.project = p.id
                            WHERE p.id = ? AND e.invoice IS NOT NULL AND e.invoice != ?
                            <sql:param value="${project.id}"/>
                            <sql:param value="${param.invoice}"/>
                        </sql:query>

                        <c:set var="previouslyBilled" value="${previouslyBilledResult.rows[0].costTotal + previouslyInvoicedExpenseResult.rows[0].costTotal + project.previouslyBilled}"/>
                        <c:if test="${empty previouslyBilled}">
                            <c:set var="previouslyBilled" value="0"/>
                        </c:if>

                        <c:set var="cumulative" value="${previouslyBilled + invoiceTotal}"/>

                        Previously Billed: <fmt:formatNumber type="currency" value="${previouslyBilled}"/><br/>
                        Cumulative: <fmt:formatNumber type="currency" value="${cumulative}"/><br/>
                        Funds Remaining: <fmt:formatNumber type="currency" value="${project.funded - cumulative}"/>
                    </c:if>
                </p>

                <c:if test="${result.rowCount > 0}">
                    <div>
                        <table id="hours" class="table">
                            <caption>Timesheet Entries</caption>
                            <thead>
                                <tr>
                                    <th rowspan="2">Employee</th>
                                    <th colspan="2">Task</th>
                                    <th rowspan="2">Line Item</th>
                                    <th rowspan="2">Labor Category</th>
                                    <th rowspan="2">Date</th>
                                    <th rowspan="2">Rate</th>
                                    <th rowspan="2">Duration</th>
                                    <th rowspan="2">Cost</th>
                                </tr>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${result.rows}">
                                    <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.task, row.date, row.duration)}"/>
                                    <tr>
                                        <td>${directory.byNumber[row.employee].fullName}</td>
                                        <td><a href="task?task_id=${row.task}">${row.task}</a></td>
                                        <td>${fn:escapeXml(row.name)}</td>
                                        <td class="line_item">${row.line_item}</td>
                                        <c:choose>
                                            <c:when test="${empty costData.laborCategory}">
                                                <td class="error">no labor category</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td>${costData.laborCategory.name}</td>
                                            </c:otherwise>
                                        </c:choose>
                                        <td class="date">${row.date}</td>
                                        <c:choose>
                                            <c:when test="${empty costData.laborCategory}">
                                                <td class="error">no rate</td>
                                                <c:set var="errorsOccurred" value="true"/>
                                            </c:when>
                                            <c:otherwise>
                                                <td class="currency"><fmt:formatNumber type="currency" value="${costData.rate}"/></td>
                                            </c:otherwise>
                                        </c:choose>
                                        <td class="duration">${row.duration}</td>
                                        <c:choose>
                                            <c:when test="${empty costData.laborCategory}">
                                                <td class="error">no rate</td>
                                                <c:set var="errorsOccurred" value="true"/>
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
                                    JOIN tasks AS t ON t.id = i.task
                                    WHERE i.id = ?
                                    <sql:param value="${param.invoice}"/>
                                </sql:query>
                                <tr>
                                    <td colspan="7"><strong>Total</strong></td>
                                    <td class="duration"><strong><fmt:formatNumber value="${sum.rows[0].total}" minFractionDigits="2"/></strong></td>
                                    <td class="currency"><strong><fmt:formatNumber type="currency" value="${laborTotal}"/></strong></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <sql:query dataSource="jdbc/sarariman" var="employees">
                        SELECT DISTINCT h.employee
                        FROM invoices AS i
                        JOIN tasks AS t ON i.task = t.id
                        JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                        WHERE i.id = ?
                        <sql:param value="${param.invoice}"/>
                    </sql:query>

                    <div id="totals">
                        <div id="task">
                            <table class="table">
                                <caption>Total by Task</caption>
                                <thead>
                                    <tr><th colspan="2">Task</th><th rowspan="2">Cost</th></tr>
                                    <tr><th>#</th><th>Name</th></tr>
                                </thead>
                                <tbody>
                                    <sql:query dataSource="jdbc/sarariman" var="tasks">
                                        SELECT DISTINCT h.task
                                        FROM invoices AS i
                                        JOIN tasks AS t ON i.task = t.id
                                        JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                                        WHERE i.id = ?
                                        <sql:param value="${param.invoice}"/>
                                    </sql:query>
                                    <c:forEach var="taskRows" items="${tasks.rows}">
                                        <tr>
                                            <td>${taskRows.task}</td>
                                            <sql:query dataSource="jdbc/sarariman" var="totals">
                                                SELECT SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal, t.name
                                                FROM invoices AS i
                                                JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                                                JOIN tasks AS t ON h.task = t.id
                                                JOIN projects AS p on p.id = t.project
                                                JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                                                JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                                                WHERE i.id = ? AND h.task = ?
                                                GROUP BY t.name
                                                <sql:param value="${param.invoice}"/>
                                                <sql:param value="${taskRows.task}"/>
                                            </sql:query>
                                            <td>${fn:escapeXml(totals.rows[0].name)}</td>
                                            <td class="currency"><fmt:formatNumber type="currency" value="${totals.rows[0].costTotal}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div id="employeeTask">
                            <table class="table">
                                <caption>Total by Employee and Task</caption>
                                <thead>
                                    <tr><th rowspan="2">Employee</th><th colspan="2">Task</th><th rowspan="2">Hours</th></tr>
                                    <tr><th>#</th><th>Name</th></tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="employeeRows" items="${employees.rows}">
                                        <sql:query dataSource="jdbc/sarariman" var="tasks">
                                            SELECT DISTINCT h.task
                                            FROM invoices AS i
                                            JOIN tasks AS t ON i.task = t.id
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
                                                    SELECT SUM(h.duration) AS total, t.name
                                                    FROM invoices AS i
                                                    JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                                                    JOIN tasks AS t ON h.task = t.id
                                                    WHERE i.id = ? AND h.employee = ? AND h.task = ?
                                                    GROUP BY t.name
                                                    <sql:param value="${param.invoice}"/>
                                                    <sql:param value="${employeeRows.employee}"/>
                                                    <sql:param value="${taskRows.task}"/>
                                                </sql:query>
                                                <td>${fn:escapeXml(totals.rows[0].name)}</td>
                                                <td class="duration"><fmt:formatNumber value="${totals.rows[0].total}" minFractionDigits="2"/></td>
                                            </tr>
                                        </c:forEach>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div id="employees">
                            <table class="table">
                                <caption>Total by Employee</caption>
                                <thead>
                                    <tr><th>Employee</th><th>Hours</th></tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="employeeRows" items="${employees.rows}">
                                        <tr>
                                            <td>${directory.byNumber[employeeRows.employee].fullName}</td>
                                            <sql:query dataSource="jdbc/sarariman" var="totals">
                                                SELECT SUM(h.duration) AS total
                                                FROM invoices AS i
                                                JOIN tasks AS t on i.task = t.id
                                                JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                                                WHERE i.id = ? AND h.employee = ?
                                                <sql:param value="${param.invoice}"/>
                                                <sql:param value="${employeeRows.employee}"/>
                                            </sql:query>
                                            <td class="duration"><fmt:formatNumber value="${totals.rows[0].total}" minFractionDigits="2"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </c:if>

                <c:if test="${expenseResultSet.rowCount > 0}">
                    <div>
                        <table class="table">
                            <caption>Expenses (ODC)</caption>
                            <thead>
                                <tr>
                                    <th rowspan="2">Date</th>
                                    <th rowspan="2">Employee</th>
                                    <th colspan="2">Task</th>
                                    <th rowspan="2">Description</th>
                                    <th rowspan="2">Cost</th>
                                </tr>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${expenseResultSet.rows}">
                                    <tr>
                                        <td>${row.date}</td>
                                        <td>${directory.byNumber[row.employee].fullName}</td>
                                        <td>${row.task}</td>
                                        <td>${fn:escapeXml(sarariman.tasks.map[row.task].name)}</td>
                                        <td>${fn:escapeXml(row.description)}</td>
                                        <td class="currency"><fmt:formatNumber type="currency" value="${row.cost}"/></td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td colspan="5"><strong>Total</strong></td>
                                    <td class="currency"><strong><fmt:formatNumber type="currency" value="${expensesTotal}"/></strong></td>
                                </tr>
                                <c:if test="${project.ODCFee > 0}">
                                    <tr>
                                        <td colspan="3"><strong>ODC Fee (<fmt:formatNumber value="${project.ODCFee}" type="percent"/>)</strong></td>
                                        <td class="currency"><strong><fmt:formatNumber type="currency" value="${odc_fee}"/></strong></td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </c:if>

                <c:if test="${servicesResultSet.rowCount > 0}">
                    <div>
                        <table class="table">
                            <caption>Services</caption>
                            <thead>
                                <tr>
                                    <th>Description</th>
                                    <th>Start</th>
                                    <th>End</th>
                                    <th>Cost</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${servicesResultSet.rows}">
                                    <tr>
                                        <td>${fn:escapeXml(row.description)}</td>
                                        <td>${row.pop_start}</td>
                                        <td>${row.pop_end}</td>
                                        <td class="currency"><fmt:formatNumber type="currency" value="${row.period_rate}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </c:if>

            <div id="controls" class="hidden-print">
                <c:if test="${fn:contains(sarariman.invoiceManagers, user)}">

                    <p>Email will go to:</p>
                    <sql:query dataSource="jdbc/sarariman" var="emailResult">
                        SELECT c.name, c.email
                        FROM project_invoice_contacts as ptc
                        JOIN projects AS p ON ptc.project = p.id
                        JOIN contacts AS c ON c.id = ptc.contact
                        WHERE p.id = ?
                        <sql:param value="${project.id}"/>
                    </sql:query>
                    <ul>
                        <c:forEach var="row" items="${emailResult.rows}"><li>${row.name} &lt;${row.email}&gt;</li></c:forEach>
                    </ul>

                    <form id="email" action="${pageContext.request.contextPath}/EmailBuilder" method="POST">
                        <c:forEach var="documentName" items="${documentNames}">
                            <input type="hidden" name="documentName" value="${documentName}"/>
                        </c:forEach>

                        <c:forEach var="documentLink" items="${documentLinks}">
                            <input type="hidden" name="documentLink" value="${fn:escapeXml(documentLink)}"/>
                        </c:forEach>

                        <input type="hidden" name="subject" value="Invoice ${param.invoice}"/>
                        <fmt:formatNumber type="currency" var="invoiceTotal" value="${invoiceTotal}"/>
                        <c:set var="body" value="Please find attached invoice ${param.invoice}.\n\n"/>
                        <c:set var="body" value="${body}Total: ${invoiceTotal}\n"/>
                        <c:set var="body" value="${body}Project: ${fn:escapeXml(project.name)}\n"/>
                        <c:set var="body" value="${body}Period: ${invoice_info.pop_start} - ${invoice_info.pop_end}\n"/>
                        <c:if test="${!empty project.contract}">
                            <c:set var="body" value="${body}Contract: ${project.contract}\n"/>
                        </c:if>
                        <c:if test="${!empty project.subcontract}">
                            <c:set var="body" value="${body}Subcontract: ${project.subcontract}\n"/>
                        </c:if>
                        <input type="hidden" name="body" value="${body}"/>

                        <input type="hidden" name="from" value="billing@stackframe.com"/>

                        <input type="hidden" name="invoiceNumber" value="${param.invoice}"/>

                        <c:forEach var="row" items="${emailResult.rows}">
                            <input type="hidden" name="to" value="${row.email}"/>
                        </c:forEach>

                        <c:forEach var="invoiceManager" items="${sarariman.invoiceManagers}">
                            <input type="hidden" name="cc" value="${invoiceManager.email}"/>
                        </c:forEach>

                        <label for="testaddress">Test Address: </label><input type="text" id="testaddress" name="testaddress"/><br/>
                        <input type="submit" value="Send" <c:if test="${errorsOccurred || empty emailResult.rows}">disabled="true"</c:if> />
                    </form>
                </c:if>

                <p>
                    <a href="${laborCSVLink}">Export labor costs as CSV</a><br/>
                    <a href="${expenseCSVLink}">Export expenses as CSV</a>
                </p>

                <c:if test="${user.administrator}">
                    <form action="invoiceController" method="post">
                        <input type="hidden" name="id" value="${param.invoice}"/>
                        <input type="submit" name="action" value="delete"/>
                    </form>
                </c:if>
            </div>

            <table id="emailLog" class="table table-rounded table-striped table-bordered hidden-print">
                <caption>Invoice email log</caption>
                <sql:query dataSource="jdbc/sarariman" var="logResult">
                    SELECT *
                    FROM invoice_email_log
                    WHERE invoice = ?
                    <sql:param value="${param.invoice}"/>
                </sql:query>
                <tr><th>Sender</th><th>Sent</th></tr>
                <c:forEach var="row" items="${logResult.rows}">
                    <tr>
                        <td>${directory.byNumber[row.sender].fullName}</td>
                        <td>${row.sent}</td>
                    </tr>
                </c:forEach>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
