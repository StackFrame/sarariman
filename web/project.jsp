<%--
  Copyright (C) 2009-2012 StackFrame, LLC
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
    <c:set var="isManager" value="${sarariman:isManager(user, project)}"/>
    <c:set var="isCostManager" value="${sarariman:isCostManager(user, project)}"/>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="utilities.js"/>
        <title>Project ${project.id}</title>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

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

            <label for="contract">Contract: </label>
            <input type="text" size="40" id="contract" name="contract" value="${fn:escapeXml(project.contract)}"/><br/>

            <label for="subcontract">Subcontract: </label>
            <input type="text" size="40" id="subcontract" name="subcontract" value="${fn:escapeXml(project.subcontract)}"/><br/>

            <c:if test="${isCostManager}">
                <label for="funded">Funded: </label>
                <input type="text" size="13" id="funded" name="funded" value="${fn:escapeXml(project.funded)}"/><br/>

                <label for="previously_billed">Previously Billed: </label>
                <input type="text" size="13" id="previously_billed" name="previously_billed" value="${fn:escapeXml(project.previouslyBilled)}"/><br/>

                <label for="terms">Terms </label>
                <input type="text" id="terms" name="terms" value="${project.terms}"/><br/>

                <label for="odc_fee">ODC Fee </label>
                <input type="text" id="odc_fee" name="odc_fee" value="${project.ODCFee}"/><br/>
            </c:if>

            <label for="pop_start">Period of Performance Start: </label>
            <input type="text" id="pop_start" name="pop_start" value="<fmt:formatDate value="${project.pop.start}" pattern="yyyy-MM-dd"/>"/>

            <label for="pop_end">End: </label>
            <input type="text" id="pop_end" name="pop_end" value="<fmt:formatDate value="${project.pop.end}" pattern="yyyy-MM-dd"/>"/><br/>

            <label for="active">Active: </label>
            <input type="checkbox" name="active" id="active" <c:if test="${project.active}">checked="true"</c:if> <c:if test="${!user.administrator}">disabled="true"</c:if>/><br/>

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

        <h2>Cost Managers</h2>
        <ul>
            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT employee
                FROM project_cost_managers
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

        <h2>Timesheet Contacts</h2>
        <ul>
            <sql:query dataSource="jdbc/sarariman" var="emailResult">
                SELECT c.name, c.email, c.id
                FROM project_timesheet_contacts as ptc
                JOIN projects AS p ON ptc.project = p.id
                JOIN contacts AS c ON c.id = ptc.contact
                WHERE p.id = ?
                <sql:param value="${project.id}"/>
            </sql:query>
            <c:forEach var="row" items="${emailResult.rows}">
                <c:url var="contactLink" value="contact">
                    <c:param name="id" value="${row.id}"/>
                </c:url>
                <c:url var="removeLink" value="removeProjectTimesheetContact.jsp">
                    <c:param name="contact" value="${row.id}"/>
                    <c:param name="project" value="${project.id}"/>
                </c:url>
                <li>
                    <a href="${contactLink}" title="go to contact details for ${row.name}">${row.name} &lt;${row.email}&gt;</a>
                    <c:if test="${user.administrator}">
                        <a href="${fn:escapeXml(removeLink)}" title="remove ${row.name} as a timesheet contact">X</a>
                    </c:if>
                </li>
            </c:forEach>
        </ul>

        <h2>Invoice Contacts</h2>
        <ul>
            <sql:query dataSource="jdbc/sarariman" var="emailResult">
                SELECT c.name, c.email, c.id
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
                <li><a href="${contactLink}" title="go to contact details for ${row.name}">${row.name} &lt;${row.email}&gt;</a></li>
            </c:forEach>
        </ul>

        <c:if test="${isCostManager}">
            <c:set var="lineItems" value="${project.lineItems}"/>
            <c:if test="${!empty lineItems}">
                <table class="altrows" id="line_items">
                    <caption>Line Items</caption>
                    <tr><th>Line Item</th><th>Description</th><th>Funded</th><th colspan="3">Expended</th></tr>
                    <tr><th colspan="3"></th><th>Hours</th><th>Dollars</th><th>Remaining</th></tr>
                    <c:set var="fundedTotal" value="0.0"/>
                    <c:set var="expendedHoursTotal" value="0.0"/>
                    <c:set var="expendedDollarsTotal" value="0.0"/>
                    <c:forEach var="lineItem" items="${lineItems}">
                        <tr>
                            <td class="line_item">${lineItem.id}</td>
                            <td>${lineItem.description}</td>
                            <td class="currency"><fmt:formatNumber type="currency" value="${lineItem.funded}"/></td>
                            <c:set var="fundedTotal" value="${fundedTotal + lineItem.funded}"/>

                            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                                SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                                FROM hours AS h
                                JOIN tasks AS t on h.task = t.id
                                JOIN projects AS p on p.id = t.project
                                JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                                JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                                WHERE t.line_item=? AND t.project=? AND t.billable=TRUE and h.duration > 0;
                                <sql:param value="${lineItem.id}"/>
                                <sql:param value="${project.id}"/>
                            </sql:query>

                            <c:set var="expendedDuration" value="${resultSet.rows[0].durationTotal}"/>
                            <c:set var="expendedCost" value="${resultSet.rows[0].costTotal}"/>
                            <td class="duration">${expendedDuration}</td>
                            <td class="currency"><fmt:formatNumber type="currency" value="${expendedCost}"/></td>
                            <c:set var="expendedDollarsTotal" value="${expendedDollarsTotal + expendedCost}"/>
                            <c:set var="expendedHoursTotal" value="${expendedHoursTotal + expendedDuration}"/>

                            <c:set var="remaining" value="${lineItem.funded - expendedCost}"/>
                            <c:choose>
                                <c:when test="${remaining < 0}"><c:set var="error" value="error"/></c:when>
                                <c:otherwise><c:set var="error" value=""/></c:otherwise>
                            </c:choose>
                            <td class="currency ${error}">
                                <fmt:formatNumber type="currency" value="${remaining}"/>                                       
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="2">Total</td>
                        <td class="currency"><fmt:formatNumber type="currency" value="${fundedTotal}"/></td>
                        <td class="duration">${expendedHoursTotal}</td>
                        <td class="currency"><fmt:formatNumber type="currency" value="${expendedDollarsTotal}"/></td>
                        <td class="currency"><fmt:formatNumber type="currency" value="${fundedTotal - expendedDollarsTotal}"/></td>
                    </tr>
                </table>
                <c:if test="${fundedTotal != project.funded}"><p class="error">Project funded amount does not match line item funding.</p></c:if>
            </c:if>                
        </c:if>

        <table class="altrows" id="tasks">
            <caption>Tasks</caption>
            <tr><th>ID</th><th>Task</th><th>Line Item</th><th>Active</th></tr>
            <c:forEach var="task" items="${project.tasks}">
                <tr>
                    <c:url var="link" value="task"><c:param name="task_id" value="${task.id}"/></c:url>
                    <td><a href="${link}">${task.id}</a></td>
                    <td><a href="${link}">${fn:escapeXml(task.name)}</a></td>
                    <td class="line_item">${task.lineItem}</td>
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
                    <c:url var="link" value="task"><c:param name="task_id" value="${row.id}"/></c:url>
                    <td><a href="${link}">${row.id}</a></td><td><a href="${link}">${fn:escapeXml(row.name)}</a></td>
                </tr>
            </c:forEach>
        </table>

        <c:if test="${isManager || user.administrator}">

            <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
            <c:set var="laborCategories" value="${sarariman.laborCategories}"/>

            <c:set var="expendedLaborTotal" value="0"/>
            <c:set var="invoicedLaborTotal" value="0"/>
            <c:set var="expendedExpenseTotal" value="0"/>
            <c:set var="invoicedExpenseTotal" value="0"/>
            <c:set var="expendedODCFeeTotal" value="0"/>
            <c:set var="invoicedODCFeeTotal" value="0"/>

            <c:if test="${isCostManager}">
                <table class="altrows" id="categories">
                    <caption>Labor Categories</caption>
                    <tr><th>Labor Category</th><th>Rate</th><th>Start</th><th>End</th><th colspan="2">Expended</th><th colspan="2">Invoiced</th></tr>
                    <tr><th colspan="4"></th><th>Hours</th><th>Dollars</th><th>Hours</th><th>Dollars</th></tr>
                    <c:forEach var="entry" items="${sarariman.laborCategories}">
                        <c:if test="${entry.value.project == project.id}">
                            <c:set var="category" value="${entry.value}"/>
                            <c:url var="catLink" value="laborcategory"><c:param name="id" value="${entry.key}"/></c:url>
                            <tr><td><a href="${catLink}">${category.name}</a></td>
                                <td class="currency"><a href="${catLink}"><fmt:formatNumber type="currency" value="${category.rate}"/></a></td>
                                <td><a href="${catLink}">${category.periodOfPerformanceStart}</a></td>
                                <td><a href="${catLink}">${category.periodOfPerformanceEnd}</a></td>

                                <sql:query dataSource="jdbc/sarariman" var="resultSet">
                                    SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                                    FROM hours AS h
                                    JOIN tasks AS t on h.task = t.id
                                    JOIN projects AS p on p.id = t.project
                                    JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                                    JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                                    WHERE c.id=? AND t.billable=TRUE and h.duration > 0;
                                    <sql:param value="${category.id}"/>
                                </sql:query>

                                <c:set var="expendedDuration" value="${resultSet.rows[0].durationTotal}"/>
                                <c:set var="expendedCost" value="${resultSet.rows[0].costTotal}"/>

                                <td class="duration">${expendedDuration}</td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${expendedCost}"/></td>
                                <c:set var="expendedLaborTotal" value="${expendedLaborTotal + expendedCost}"/>

                                <sql:query dataSource="jdbc/sarariman" var="resultSet">
                                    SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                                    FROM hours AS h
                                    JOIN invoices AS i ON i.task = h.task AND i.employee = h.employee AND i.date = h.date
                                    JOIN tasks AS t on h.task = t.id
                                    JOIN projects AS p on p.id = t.project
                                    JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                                    JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                                    WHERE c.id=? AND t.billable=TRUE AND h.duration > 0;
                                    <sql:param value="${category.id}"/>
                                </sql:query>

                                <c:set var="invoicedDuration" value="${resultSet.rows[0].durationTotal}"/>
                                <c:set var="invoicedCost" value="${resultSet.rows[0].costTotal}"/>

                                <td class="duration">${invoicedDuration}</td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${invoicedCost}"/></td>
                                <c:set var="invoicedLaborTotal" value="${invoicedLaborTotal + invoicedCost}"/>

                            </tr>
                        </c:if>
                    </c:forEach>
                </table>
            </c:if>

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

            <c:if test="${isCostManager}">
                <sql:query dataSource="jdbc/sarariman" var="invoiceInfoResultSet">
                    SELECT id
                    FROM invoice_info
                    WHERE project = ?
                    ORDER BY id DESC
                    <sql:param value="${project.id}"/>
                </sql:query>
                <h2>Invoices</h2>
                <table class="altrows" id="invoices_new">
                    <tr><th>ID</th></tr>
                    <c:forEach var="invoice" items="${invoiceInfoResultSet.rows}">
                        <tr>
                            <c:url var="link" value="invoice">
                                <c:param name="invoice" value="${invoice.id}"/>
                            </c:url>
                            <td><a href="${link}">${invoice.id}</a></td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>

            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT h.employee, h.date, h.duration, t.project
                FROM hours AS h
                JOIN tasks AS t on h.task = t.id
                JOIN projects AS p ON t.project = p.id
                WHERE p.id = ? AND t.billable = TRUE AND h.duration > 0
                <sql:param value="${project.id}"/>
            </sql:query>
            <c:forEach var="row" items="${result.rows}">
                <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                <c:if test="${empty costData.laborCategory}">
                    <c:set var="missingLaborCategory" value="true"/>
                    <p class="error">Labor category or billing rate missing for ${sarariman.directory.byNumber[row.employee].fullName} on ${row.date}!</p>
                </c:if>
            </c:forEach>

            <c:if test="${isCostManager}">
                <sql:query dataSource="jdbc/sarariman" var="expenseResultSet">
                    SELECT e.cost, e.invoice
                    FROM expenses AS e
                    JOIN tasks AS t on t.id = e.task
                    JOIN projects AS p ON t.project = p.id
                    WHERE p.id = ?
                    <sql:param value="${project.id}"/>
                </sql:query>
                <c:forEach var="row" items="${expenseResultSet.rows}">
                    <c:set var="expendedExpenseTotal" value="${expendedExpenseTotal + row.cost}"/>
                    <c:if test="${!empty row.invoice}">
                        <c:set var="invoicedExpenseTotal" value="${invoicedExpenseTotal + row.cost}"/>
                    </c:if>
                </c:forEach>
            </c:if>

            <c:if test="${project.ODCFee > 0}">
                <!-- FIXME: Need to do this with proper rounding. -->
                <c:set var="expendedODCFeeTotal" value="${expendedExpenseTotal * project.ODCFee}"/>
                <c:set var="invoicedODCFeeTotal" value="${invoicedExpenseTotal * project.ODCFee}"/>
            </c:if>

            <c:set var="invoicedTotal" value="${invoicedLaborTotal + invoicedExpenseTotal + expendedODCFeeTotal + project.previouslyBilled}"/>
            <c:set var="expendedTotal" value="${expendedLaborTotal + expendedExpenseTotal + invoicedODCFeeTotal + project.previouslyBilled}"/>

            <c:if test="${isCostManager}">
                <h2>Cumulative</h2>
                <table class="altrows">
                    <tr>
                        <td>Invoiced:</td><td><fmt:formatNumber type="currency" value="${invoicedTotal}"/></td>
                        <td>Remaining from invoiced:</td><td><fmt:formatNumber type="currency" value="${project.funded - invoicedTotal}"/></td>
                    </tr>
                    <tr>
                        <td>Expended:</td><td><fmt:formatNumber type="currency" value="${expendedTotal}"/></td>
                        <td>Remaining from expended:</td><td><fmt:formatNumber type="currency" value="${project.funded - expendedTotal}"/></td>
                    </tr>
                </table>
            </c:if>

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

        <c:if test="${isCostManager}">
            <c:url var="projectBilled" value="projectBilled">
                <c:param name="project" value="${param.id}"/>
            </c:url>
            <a href="${fn:escapeXml(projectBilled)}">Weekly Billing Report</a>

            <p>
                <a href="uninvoiced?project=${project_id}">Uninvoiced hours and services</a>
            </p>
        </c:if>

        <%@include file="footer.jsp" %>
    </body>
</html>
