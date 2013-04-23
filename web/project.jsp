<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>
    <fmt:parseNumber var="project_id" value="${param.id}"/>
    <c:set var="project" value="${sarariman.projects.map[project_id]}"/>
    <c:set var="isManager" value="${sarariman:isManager(user, project)}"/>
    <c:set var="isCostManager" value="${sarariman:isCostManager(user, project)}"/>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>${fn:escapeXml(project.name)} (project ${project.id})</title>
    </head>
    <body>
        <div class="container">
            <%@include file="header.jsp" %>

            <h1>${fn:escapeXml(project.name)} (project ${project.id})</h1>

            <c:if test="${user.administrator || isManager || isCostManager}">
                <form method="POST" action="projectController">
                    <input type="hidden" name="action" value="update"/>
                    <input type="hidden" name="id" value="${project.id}"/>

                    <label for="name">Name: </label>
                    <input type="text" size="40" id="name" name="name" value="${fn:escapeXml(project.name)}"/><br/>

                    <label for="customer">Client: </label>
                    <select id="customer" name="customer">
                        <c:forEach var="client" items="${sarariman.clients.all}">
                            <option value="${client.id}" <c:if test="${client.id == project.client.id}">selected="selected"</c:if>>${fn:escapeXml(client.name)}</option>
                        </c:forEach>
                    </select><br/>

                    <label for="contract">Contract: </label>
                    <input type="text" size="40" id="contract" name="contract" value="${fn:escapeXml(project.contract)}"/><br/>

                    <label for="subcontract">Subcontract: </label>
                    <input type="text" size="40" id="subcontract" name="subcontract" value="${fn:escapeXml(project.subcontract)}"/><br/>

                    <label for="purchaseOrder">Purchase Order: </label>
                    <input type="text" size="40" id="purchaseOrder" name="subcontract" value="${fn:escapeXml(project.purchaseOrder)}"/><br/>

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
                    <input type="text" id="pop_start" name="pop_start" value="<fmt:formatDate value="${project.poP.start}" pattern="yyyy-MM-dd"/>"/>

                    <label for="pop_end">End: </label>
                    <input type="text" id="pop_end" name="pop_end" value="<fmt:formatDate value="${project.poP.end}" pattern="yyyy-MM-dd"/>"/><br/>

                    <label for="active">Active: </label>
                    <input type="checkbox" name="active" id="active" <c:if test="${project.active}">checked="true"</c:if> <c:if test="${!user.administrator}">disabled="true"</c:if>/><br/>

                    <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                </form>
            </c:if>

            <c:set var="resources" value="${project.resources}"/>
            <c:if test="${!empty resources}">
                <h2>Links</h2>
                <ul>
                    <c:forEach var="resource" items="${resources}">
                        <li><a href="${fn:escapeXml(resource.URL)}">${fn:escapeXml(resource.name)}</a></li>
                    </c:forEach>
                </ul>
            </c:if>

            <h2>Audits</h2>
            <ol>
                <c:forEach var="audit" items="${project.audits}">
                    <c:set var="auditResults" value="${audit.results}"/>
                    <c:if test="${not empty auditResults}">
                        <li>
                            ${audit.displayName}
                            <ol>
                                <c:forEach var="auditResult" items="${auditResults}">
                                    <li class="error"><a href="${auditResult.URL}">${auditResult.message}</a></li>
                                </c:forEach>
                            </ol>
                        </li>
                    </c:if>
                </c:forEach>
            </ol>

            <c:url var="timeReportsLink" value="timereportsforproject.jsp">
                <c:param name="project" value="${param.id}"/>
            </c:url>
            <p><a href="${timeReportsLink}">Time Reports</a></p>

            <c:if test="${user.administrator || isCostManager}">
                <sql:query dataSource="jdbc/sarariman" var="result">
                    SELECT DATEDIFF(NOW(), sent) AS days
                    FROM invoice_info
                    WHERE project = ?
                    ORDER BY sent DESC LIMIT 1;
                    <sql:param value="${project.id}"/>
                </sql:query>
                <c:choose>
                    <c:when test="${!empty result.rows}">
                        <p>Days since last invoice: ${result.rows[0].days}</p>
                    </c:when>
                    <c:otherwise>
                        <p class="error">Not yet invoiced.</p>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <h2>Managers</h2>
            <ul>
                <c:forEach var="manager" items="${project.managers}">
                    <li>
                        <a href="${manager.URL}">${manager.fullName}</a>
                        <a href="${manager.URL}"><img width="25" height="25" onerror="this.style.display='none'" src="${manager.photoURL}"/></a>
                    </li>
                </c:forEach>
            </ul>

            <h2>Cost Managers</h2>
            <ul>
                <c:forEach var="manager" items="${project.costManagers}">
                    <li>
                        <a href="${manager.URL}">${manager.fullName}</a>
                        <a href="${manager.URL}"><img width="25" height="25" onerror="this.style.display='none'" src="${manager.photoURL}"/></a>
                    </li>
                </c:forEach>
            </ul>

            <c:set var="assistants" value="${project.administrativeAssistants}"/>
            <c:if test="${not empty assistants}">
                <h2>Administrative Assistants</h2>
                <ul>
                    <c:forEach var="assistant" items="${assistants}">
                        <li>
                            <a href="${assistant.URL}">${assistant.displayName}</a>
                            <a href="${assistant.URL}"><img width="25" height="25" onerror="this.style.display='none'" src="${assistant.photoURL}"/></a>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <c:if test="${isManager || isCostManager}">
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
            </c:if>

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT v.employee, v.begin, v.end, v.comment
                FROM vacation AS v
                JOIN task_assignments AS ta ON ta.employee = v.employee
                JOIN tasks AS t on t.id = ta.task
                JOIN projects AS p ON p.id = t.project
                WHERE p.id = ? AND (begin >= DATE(NOW()) OR end >= DATE(NOW()))
                GROUP BY v.employee, v.begin, v.end, v.comment
                ORDER BY v.begin
                <sql:param value="${param.id}"/>
            </sql:query>
            <c:if test="${resultSet.rowCount != 0}">
                <h2>Scheduled Vacation</h2>
                <ul>
                    <c:forEach var="row" items="${resultSet.rows}">
                        <li>
                            ${directory.byNumber[row.employee].fullName}:
                            <c:choose>
                                <c:when test="${row.begin eq row.end}">
                                    <fmt:formatDate value="${row.begin}" type="date" dateStyle="long" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${row.begin}" type="date" dateStyle="long" /> -
                                    <fmt:formatDate value="${row.end}" type="date" dateStyle="long" />
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${!empty row.comment}">
                                - ${fn:escapeXml(row.comment)}
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <c:set var="oof" value="${project.upcomingOutOfOffice}"/>
            <c:if test="${not empty oof}">
                <h2>Scheduled Out of Office</h2>
                <ul>
                    <c:forEach var="entry" items="${oof}">
                        <li>
                            ${entry.employee.displayName}:
                            <fmt:formatDate value="${entry.begin}" type="both" dateStyle="long" timeStyle="short" /> -
                            <fmt:parseDate var="beginDate" pattern="yyyy-MM-dd" value="${entry.begin}"/>
                            <fmt:parseDate var="endDate" pattern="yyyy-MM-dd" value="${entry.end}"/>
                            <c:choose>
                                <c:when test="${beginDate eq endDate}">
                                    <fmt:formatDate value="${entry.end}" type="time" timeStyle="short" />                                    
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${entry.end}" type="both" dateStyle="long" timeStyle="short" />                                
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${!empty entry.comment}">
                                - ${fn:escapeXml(entry.comment)}
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <c:set var="lineItems" value="${project.lineItems}"/>
            <c:if test="${isCostManager}">
                <c:if test="${!empty lineItems}">
                    <table id="line_items">
                        <caption>Line Items</caption>
                        <tr>
                            <th rowspan="2">Line Item</th>
                            <th rowspan="2">Description</th>
                            <th colspan="2">Period of Performance</th>
                            <th rowspan="2">Funded</th>
                            <th colspan="4">Invoiced</th>
                            <th colspan="4">Expended</th>
                            <th rowspan="2"></th>
                        </tr>
                        <tr>
                            <th>Start</th>
                            <th>End</th>
                            <th>Hours</th>
                            <th>$</th>
                            <th>%</th>
                            <th>Remaining</th>
                            <th>Hours</th>
                            <th>$</th>
                            <th>%</th>
                            <th>Remaining</th>
                        </tr>
                        <c:set var="fundedTotal" value="0.0"/>
                        <c:set var="invoicedHoursTotal" value="0.0"/>
                        <c:set var="invoicedDollarsTotal" value="0.0"/>
                        <c:set var="expendedHoursTotal" value="0.0"/>
                        <c:set var="expendedDollarsTotal" value="0.0"/>
                        <c:forEach var="lineItem" items="${lineItems}">
                            <tr>
                                <td class="line_item">${lineItem.id}</td>
                                <td>${fn:escapeXml(lineItem.description)}</td>
                                <td class="date"><fmt:formatDate value="${lineItem.pop.start}" pattern="yyyy-MM-dd"/></td>
                                <td class="date"><fmt:formatDate value="${lineItem.pop.end}" pattern="yyyy-MM-dd"/></td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${lineItem.funded}"/></td>
                                <c:set var="fundedTotal" value="${fundedTotal + lineItem.funded}"/>

                                <sql:query dataSource="jdbc/sarariman" var="resultSet">
                                    SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                                    FROM hours AS h
                                    JOIN tasks AS t on h.task = t.id
                                    JOIN projects AS p on p.id = t.project
                                    JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                                    JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                                    JOIN invoices AS i ON i.task = h.task AND i.employee = h.employee AND i.date = h.date
                                    WHERE t.line_item=? AND t.project=? AND t.billable=TRUE and h.duration > 0;
                                    <sql:param value="${lineItem.id}"/>
                                    <sql:param value="${project.id}"/>
                                </sql:query>

                                <c:set var="invoicedDuration" value="${resultSet.rows[0].durationTotal}"/>
                                <c:set var="invoicedCost" value="${resultSet.rows[0].costTotal}"/>
                                <td class="duration"><fmt:formatNumber value="${invoicedDuration}" minFractionDigits="2"/></td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${invoicedCost}"/></td>
                                <c:choose>
                                    <c:when test="${lineItem.funded lt 0.1}">
                                        <td>NaN</td>
                                    </c:when>
                                    <c:otherwise>
                                        <td class="percentage"><fmt:formatNumber value="${invoicedCost / lineItem.funded}" type="percent"/></td>                                    
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="invoicedDollarsTotal" value="${invoicedDollarsTotal + invoicedCost}"/>
                                <c:set var="invoicedHoursTotal" value="${invoicedHoursTotal + invoicedDuration}"/>

                                <c:set var="remaining" value="${lineItem.funded - invoicedCost}"/>
                                <c:choose>
                                    <c:when test="${remaining < 0}"><c:set var="error" value="error"/></c:when>
                                    <c:otherwise><c:set var="error" value=""/></c:otherwise>
                                </c:choose>
                                <td class="currency ${error}">
                                    <fmt:formatNumber type="currency" value="${remaining}"/>                                       
                                </td>

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
                                <td class="duration"><fmt:formatNumber value="${expendedDuration}" minFractionDigits="2"/></td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${expendedCost}"/></td>
                                <c:choose>
                                    <c:when test="${lineItem.funded lt 0.1}">
                                        <td>NaN</td>
                                    </c:when>
                                    <c:otherwise>
                                        <td class="percentage"><fmt:formatNumber value="${expendedCost / lineItem.funded}" type="percent"/></td>                                    
                                    </c:otherwise>
                                </c:choose>
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

                                <td>   
                                    <form style="display:inline" method="GET" action="lineItems/edit.jsp">
                                        <input type="hidden" name="id" value="${lineItem.id}"/>
                                        <input type="hidden" name="project" value="${project.id}"/>
                                        <input type="submit" name="Edit" value="edit" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td colspan="4"><strong>Total</strong></td>
                            <td class="currency"><strong><fmt:formatNumber type="currency" value="${fundedTotal}"/></strong></td>
                            <td class="duration"><strong><fmt:formatNumber value="${invoicedHoursTotal}" minFractionDigits="2"/></strong></td>
                            <td class="currency"><strong><fmt:formatNumber type="currency" value="${invoicedDollarsTotal}"/></strong></td>
                            <td class="percentage"><strong><fmt:formatNumber value="${invoicedDollarsTotal / fundedTotal}" type="percent"/></strong></td> <!-- FIXME: Need to use expended_warning_threshold to flag error. --> <!-- FIXME: Add to audit list. -->
                            <td class="currency"><strong><fmt:formatNumber type="currency" value="${fundedTotal - invoicedDollarsTotal}"/></strong></td>
                            <td class="duration"><strong><fmt:formatNumber value="${expendedHoursTotal}" minFractionDigits="2"/></strong></td>
                            <td class="currency"><strong><fmt:formatNumber type="currency" value="${expendedDollarsTotal}"/></strong></td>
                            <td class="percentage"><strong><fmt:formatNumber value="${expendedDollarsTotal / fundedTotal}" type="percent"/></strong></td> <!-- FIXME: Need to use expended_warning_threshold to flag error. --> <!-- FIXME: Add to audit list. -->
                            <td class="currency"><strong><fmt:formatNumber type="currency" value="${fundedTotal - expendedDollarsTotal}"/></strong></td>
                            <td></td>
                        </tr>
                    </table>
                    <c:if test="${fundedTotal != project.funded}"><p class="error">Project funded amount does not match line item funding.</p></c:if>
                    <c:if test="${project.expended != expendedDollarsTotal}"><p class="error">Project expended amount does not match line item expended.</p></c:if>
                </c:if>                
            </c:if>

            <c:if test="${isManager || isCostManager}">
                <table id="tasks">
                    <caption>Tasks</caption>
                    <tr>
                        <th rowspan="2">ID</th>
                        <th rowspan="2">Task</th>
                        <c:if test="${!empty lineItems}">
                            <th rowspan="2">Line Item</th>
                        </c:if>
                        <th rowspan="2">Active</th>
                        <th colspan="2">Invoiced</th>
                        <th colspan="2">Expended</th>
                    </tr>
                    <tr>
                        <th>Hours</th>
                        <th>$</th>
                        <th>Hours</th>
                        <th>$</th>
                    </tr>
                    <c:forEach var="task" items="${project.tasks}">
                        <tr>
                            <td><a href="${task.URL}">${task.id}</a></td>
                            <td><a href="${task.URL}">${fn:escapeXml(task.name)}</a></td>
                            <c:if test="${!empty lineItems}">
                                <td class="line_item">${task.lineItem}</td>
                            </c:if>
                            <td>
                                <form>
                                    <input type="checkbox" name="active" disabled="true" <c:if test="${task.active}">checked="checked"</c:if>/>
                                </form>
                            </td>
                            <td class="duration"><fmt:formatNumber value="${task.invoicedHours}" minFractionDigits="2"/></td>
                            <td class="currency"><fmt:formatNumber type="currency" value="${task.invoiced}"/></td>
                            <td class="duration"><fmt:formatNumber value="${task.expendedHours}" minFractionDigits="2"/></td>
                            <td class="currency"><fmt:formatNumber type="currency" value="${task.expended}"/></td>
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
                <table id="task_assignments">
                    <caption>Task Assignments</caption>
                    <tr><th>Employee</th><th colspan="2">Task</th></tr>
                    <c:forEach var="row" items="${resultSet.rows}">
                        <tr>
                            <!-- FIXME: Join rows with the same task together. -->
                            <td><a href="${sarariman.directory.byNumber[row.employee].URL}">${sarariman.directory.byNumber[row.employee].fullName}</a></td>
                            <td><a href="${sarariman.tasks.map[row.id].URL}">${row.id}</a></td><td><a href="${link}">${fn:escapeXml(row.name)}</a></td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>

            <c:if test="${isManager || user.administrator || isCostManager}">

                <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
                <c:set var="laborCategories" value="${sarariman.laborCategories}"/>

                <c:set var="expendedLaborTotal" value="0"/>
                <c:set var="invoicedLaborTotal" value="0"/>
                <c:set var="expendedExpenseTotal" value="0"/>
                <c:set var="invoicedExpenseTotal" value="0"/>
                <c:set var="expendedODCFeeTotal" value="0"/>
                <c:set var="invoicedODCFeeTotal" value="0"/>

                <c:if test="${isCostManager}">
                    <table id="categories">
                        <caption>Labor Categories</caption>
                        <!-- FIXME: Sort by date and then rate. Check out project 49 for example where this is needed. -->
                        <tr><th>Labor Category</th><th>Rate</th><th>Start</th><th>End</th><th colspan="2">Expended</th><th colspan="2">Invoiced</th></tr>
                        <tr><th colspan="4"></th><th>Hours</th><th>$</th><th>Hours</th><th>$</th></tr>
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

                                    <td class="duration"><fmt:formatNumber minFractionDigits="2" value="${expendedDuration}"/></td>
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

                                    <td class="duration"><fmt:formatNumber minFractionDigits="2" value="${invoicedDuration}"/></td>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${invoicedCost}"/></td>
                                    <c:set var="invoicedLaborTotal" value="${invoicedLaborTotal + invoicedCost}"/>

                                </tr>
                            </c:if>
                        </c:forEach>
                    </table>
                </c:if>

                <table id="rates">
                    <caption>Labor Category Assignments</caption>
                    <tr><th>Employee</th><th>Labor Category</th><th>Start</th><th>End</th><th>Rate</th></tr>
                    <c:forEach var="entry" items="${sarariman.projectBillRates}">
                        <c:set var="laborCategory" value="${sarariman.laborCategories[entry.laborCategory]}"/>
                        <c:if test="${laborCategory.project == project.id}">
                            <tr>
                                <td><a href="${entry.employee.URL}">${entry.employee.fullName}</a></td>
                                <c:url var="assignmentURL" value="laborcategoryassignment.jsp">
                                    <c:param name="id" value="${entry.id}"/>
                                </c:url>
                                <td><a href="${assignmentURL}">${laborCategory.name}</a></td>
                                <td>${entry.periodOfPerformanceStart}</td>
                                <td>${entry.periodOfPerformanceEnd}</td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${laborCategory.rate}"/></td>
                                <c:if test="${entry.periodOfPerformanceStart lt laborCategory.periodOfPerformanceStart or entry.periodOfPerformanceEnd gt laborCategory.periodOfPerformanceEnd}">
                                    <td class="error">Labor Category Period of Performance is not valid!</td>
                                </c:if>
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
                    <table id="invoices_new">
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
                    SELECT h.employee, h.date, h.duration, t.project, h.task
                    FROM hours AS h
                    JOIN tasks AS t on h.task = t.id
                    JOIN projects AS p ON t.project = p.id
                    WHERE p.id = ? AND t.billable = TRUE AND h.duration > 0
                    <sql:param value="${project.id}"/>
                </sql:query>
                <c:forEach var="row" items="${result.rows}">
                    <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.task, row.date, row.duration)}"/>
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
                    <table>
                        <c:choose>
                            <c:when test="${empty project.funded or project.funded lt 0.1}"> <!-- FIXME: This should check for a funding limit. -->
                                <tr>
                                    <th></th>
                                    <th>Billed</th>
                                </tr>
                                <tr>
                                    <td>Invoiced</td>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${invoicedTotal}"/></td>
                                </tr>
                                <tr>
                                    <td>Expended</td>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${expendedTotal}"/></td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <th></th>
                                    <th colspan="2">Billed</th>
                                    <th colspan="2">Remaining</th>
                                </tr>
                                <tr>
                                    <td>Invoiced</td>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${invoicedTotal}"/></td>
                                    <td class="percentage"><fmt:formatNumber type="percent" value="${invoicedTotal / project.funded}"/></td>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${project.funded - invoicedTotal}"/></td>
                                    <td class="percentage"><fmt:formatNumber type="percent" value="${(project.funded - invoicedTotal) / project.funded}"/></td>
                                </tr>
                                <tr>
                                    <td>Expended</td>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${expendedTotal}"/></td>
                                    <td class="percentage"><fmt:formatNumber type="percent" value="${expendedTotal / project.funded}"/></td>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${project.funded - expendedTotal}"/></td>
                                    <td class="percentage"><fmt:formatNumber type="percent" value="${(project.funded - expendedTotal) / project.funded}"/></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </table>
                </c:if>

                <c:if test="${missingLaborCategory}">
                    <p class="error">There are labor categories missing from this project which are causing them to be excluded from expended amount!</p>
                </c:if>

            </c:if>

            <c:if test="${isManager || isCostManager}">
                <p>
                    <c:url var="hoursByProject" value="hoursByProject">
                        <c:param name="project" value="${param.id}"/>
                    </c:url>
                    <a href="${hoursByProject}">Hours billed to this project</a>
                </p>
            </c:if>

            <c:if test="${isCostManager}">
                <p>
                    <a href="${fn:escapeXml(project.projectedExpenses.URL)}">Projection</a><br/>

                    <c:url var="projectBilled" value="projectBilled">
                        <c:param name="project" value="${param.id}"/>
                    </c:url>
                    <a href="${fn:escapeXml(projectBilled)}">Weekly Billing Report</a><br/>

                    <a href="uninvoiced?project=${project_id}">Uninvoiced hours and services</a>
                </p>
            </c:if>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
