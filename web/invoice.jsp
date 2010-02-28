<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${!(user.administrator || user.invoiceManager)}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <style type="text/css">
            @media screen, print{
                body {
                    margin: 0px auto;
                    padding: 15px;
                    background: white;
                    color: black;
                }

                p.error {
                    color: red;
                }

                #footer {
                    text-align: center;
                }

                .duration {
                    text-align: right;
                }

                table {
                    border-width: 1px;
                    border-style: outset;
                }

                table td, table th {
                    border-width: 1px;
                    border-style: inset;
                }
            }

            @media print{
                a {
                    color: #000;
                    text-decoration: none;
                }

                #topnav {
                    display: none;
                }

                #timesheets {
                    display: none;
                }

                #csv {
                    display: none;
                }

                #email {
                    display: none;
                }

                table {
                    font-size: 12px;
                    text-align: center;
                }

                table td, table th {
                    padding: 5px;
                }
            }
        </style>
        <title>Invoice ${param.invoice}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <jsp:useBean id="documentNames" class="java.util.ArrayList" scope="request"/>
        <jsp:useBean id="documentLinks" class="java.util.ArrayList" scope="request"/>

        <h1>Invoice ${param.invoice}</h1>

        <c:if test="${user.administrator}">
            <form action="invoiceController" method="post">
                <input type="hidden" name="id" value="${param.invoice}"/>
                <input type="submit" name="action" value="delete"/>
            </form>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="invoice_info_result">
            SELECT project, sent, customer, payment_received, pop_start, pop_end
            FROM invoice_info AS i
            WHERE i.id = ?
            <sql:param value="${param.invoice}"/>
        </sql:query>
        <c:set var="invoice_info" value="${invoice_info_result.rows[0]}"/>
        <c:set var="project" value="${sarariman.projects[invoice_info.project]}"/>
        <c:set var="customer" value="${sarariman.customers[invoice_info.customer]}"/>
        <fmt:formatDate var="sent" value="${invoice_info.sent}"/>
        <fmt:formatDate var="received" value="${invoice_info.payment_received}"/>

        <p>Customer: ${fn:escapeXml(customer.name)}<br/>
            Project: ${fn:escapeXml(project.name)}<br/>
            Sent: ${sent}<br/>
            Period of Performance: Start: ${invoice_info.pop_start} End: ${invoice_info.pop_end}<br/>
            Payment received: ${received}</p>
        <p>Description: ${fn:escapeXml(invoice_info.description)}</p>
        <p>Comments: ${fn:escapeXml(invoice_info.comments)}</p>

        <%
        documentNames.add("Invoice-" + request.getParameter("invoice") + ".pdf");
        documentLinks.add(String.format("invoice?outputType=pdf&invoice=%s", request.getParameter("invoice")));
        %>

        <div id="timesheets">
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
                        </c:url>
                        <li><a href="${fn:escapeXml(html)}">${directory.byNumber[row.employee].fullName} ${formattedWeek}</a>
                            <a href="${fn:escapeXml(pdf)}">[PDF]</a>
                            <c:set var="documentName" value="${directory.byNumber[row.employee].fullName} ${formattedWeek}.pdf"/>
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
        <c:set var="csvLinkEmitted" value="${false}" scope="request"/>
        <c:forEach var="extension" items="${sarariman.extensions}">
            <jsp:include page="${extension.invoiceInclude}">
                <jsp:param name="invoice" value="${param.invoice}"/>
            </jsp:include>
        </c:forEach>

        <c:if test="${!csvLinkEmitted}">
            <p id="csv"><a href="laborcosts.csv?id=${param.invoice}">Download as CSV</a></p>
            <%
        documentNames.add(String.format("laborcosts-%s.csv", request.getParameter("invoice")));
        documentLinks.add(String.format("laborcosts.csv?id=%s", request.getParameter("invoice")));
            %>
        </c:if>


        <c:if test="${!entriesTableEmitted}">
            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT i.employee, i.task, i.date, h.duration, t.project
                FROM invoices AS i
                JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                JOIN tasks AS t on i.task = t.id
                JOIN projects AS p ON t.project = p.id
                WHERE i.id = ? AND t.billable = TRUE
                ORDER BY h.date ASC, h.employee ASC, h.task ASC
                <sql:param value="${param.invoice}"/>
            </sql:query>
            <br/>
            <table class="altrows" id="hours">
                <caption>Entries</caption>
                <tbody>
                    <tr>
                        <th>Employee</th>
                        <th>Task</th>
                        <th>Labor Category</th>
                        <th>Date</th>
                        <th>Rate</th>
                        <th>Duration</th>
                        <th>Cost</th>
                    </tr>
                    <c:set var="totalCost" value="0"/>
                    <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
                    <c:set var="laborCategories" value="${sarariman.laborCategories}"/>
                    <c:forEach var="row" items="${result.rows}">
                        <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                        <c:set var="totalCost" value="${totalCost + costData.cost}"/>
                        <tr>
                            <td>${directory.byNumber[row.employee].fullName}</td>
                            <td><a href="task?task_id=${row.task}">${row.task}</a></td>
                            <c:choose>
                                <c:when test="${empty costData.laborCategory}">
                                    <td class="error">no labor category</td>
                                </c:when>
                                <c:otherwise>
                                    <td>${costData.laborCategory.name}</td>
                                </c:otherwise>
                            </c:choose>
                            <td>${row.date}</td>
                            <c:choose>
                                <c:when test="${empty costData.laborCategory}">
                                    <td class="error">no rate</td>
                                </c:when>
                                <c:otherwise>
                                    <td class="currency"><fmt:formatNumber type="currency" value="${costData.rate}"/></td>
                                </c:otherwise>
                            </c:choose>
                            <td class="duration">${row.duration}</td>
                            <c:choose>
                                <c:when test="${empty costData.laborCategory}">
                                    <td class="error">no rate</td>
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
                        WHERE i.id = ?
                        <sql:param value="${param.invoice}"/>
                    </sql:query>
                    <tr>
                        <td colspan="5"><strong>Total</strong></td>
                        <td class="duration"><strong>${sum.rows[0].total}</strong></td>
                        <td class="currency"><strong><fmt:formatNumber type="currency" value="${totalCost}"/></strong></td>
                    </tr>
                    <c:set var="invoiceTotal" value="${totalCost}" scope="request"/>
                </tbody>
            </table>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="employees">
            SELECT DISTINCT h.employee
            FROM invoices AS i
            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            WHERE i.id = ?
            <sql:param value="${param.invoice}"/>
        </sql:query>

        <br/>
        <table class="altrows">
            <caption>Total by Employee and Task</caption>
            <tbody>
                <tr><th>Employee</th><th>Task</th><th>Total</th></tr>
                <c:forEach var="employeeRows" items="${employees.rows}">
                    <sql:query dataSource="jdbc/sarariman" var="tasks">
                        SELECT DISTINCT h.task
                        FROM invoices AS i
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
                                SELECT SUM(h.duration) AS total
                                FROM invoices AS i
                                JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                                WHERE i.id = ? AND h.employee = ? AND h.task = ?
                                <sql:param value="${param.invoice}"/>
                                <sql:param value="${employeeRows.employee}"/>
                                <sql:param value="${taskRows.task}"/>
                            </sql:query>
                            <td class="duration">${totals.rows[0].total}</td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>

        <br/>
        <table class="altrows">
            <caption>Total by Employee</caption>
            <tbody>
                <tr><th>Employee</th><th>Total</th></tr>
                <c:forEach var="employeeRows" items="${employees.rows}">
                    <tr>
                        <td>${directory.byNumber[employeeRows.employee].fullName}</td>
                        <sql:query dataSource="jdbc/sarariman" var="totals">
                            SELECT SUM(h.duration) AS total
                            FROM invoices AS i
                            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                            WHERE i.id = ? AND h.employee = ?
                            <sql:param value="${param.invoice}"/>
                            <sql:param value="${employeeRows.employee}"/>
                        </sql:query>
                        <td class="duration">${totals.rows[0].total}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <br/>
        <form id="email" action="${pageContext.request.contextPath}/EmailBuilder" method="POST">
            <c:forEach var="documentName" items="${documentNames}">
                <input type="hidden" name="documentName" value="${documentName}"/>
            </c:forEach>

            <c:forEach var="documentLink" items="${documentLinks}">
                <input type="hidden" name="documentLink" value="${fn:escapeXml(documentLink)}"/>
            </c:forEach>

            <input type="hidden" name="subject" value="Invoice ${param.invoice}"/>
            <fmt:formatNumber type="currency" var="invoiceTotal" value="${invoiceTotal}"/>
            <input type="hidden" name="body" value="Please find attached documents for invoice ${param.invoice} for ${invoiceTotal}."/>

            <label for="to">Email Address: </label><input type="text" id="to" name="to"/><br/>
            <input type="submit" value="Send"/>
        </form>

        <%@include file="footer.jsp" %>
    </body>
</html>
