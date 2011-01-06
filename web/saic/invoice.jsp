<%--
  Copyright (C) 2009-2011 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${!(user.administrator || user.invoiceManager || isProjectManager)}">
    <jsp:forward page="../unauthorized"/>
</c:if>

<jsp:useBean id="documentNames" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="documentLinks" class="java.util.ArrayList" scope="request"/>

<sql:query dataSource="jdbc/sarariman" var="customerResult">
    SELECT p.customer
    FROM invoice_info AS i
    JOIN projects AS p ON i.project = p.id
    WHERE i.id=?
    <sql:param value="${param.invoice}"/>
</sql:query>
<c:set var="isSAIC" value="${customerResult.rows[0].customer == 1}"/>

<c:if test="${isSAIC}">
    <c:set var="entriesTableEmitted" value="${true}" scope="request"/>

    <sql:query dataSource="jdbc/sarariman" var="result">
        SELECT i.employee, i.task, t.project, i.date, h.duration, s.po_line_item, s.charge_number
        FROM invoices AS i
        JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
        JOIN tasks AS t on t.id = i.task
        LEFT JOIN saic_tasks AS s ON i.task = s.task
        WHERE i.id = ? AND t.billable = TRUE
        ORDER BY s.po_line_item ASC, h.employee ASC, h.date ASC, h.task ASC, s.charge_number ASC
        <sql:param value="${param.invoice}"/>
    </sql:query>

    <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
    <c:set var="laborCategories" value="${sarariman.laborCategories}"/>

    <c:set var="invoiceTotal" value="0" scope="request"/>
    <c:set var="laborTotal" value="0" scope="request"/>
    <c:set var="expensesTotal" value="0" scope="request"/>

    <c:forEach var="row" items="${result.rows}">
        <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
        <c:set var="laborTotal" value="${laborTotal + costData.cost}" scope="request"/>
    </c:forEach>

    <sql:query dataSource="jdbc/sarariman" var="expenseResultSet">
        SELECT e.employee, e.date, e.cost, e.description, s.po_line_item
        FROM expenses AS e
        JOIN tasks AS t on t.id = e.task
        LEFT JOIN saic_tasks AS s ON e.task = s.task
        WHERE e.invoice = ?
        ORDER BY s.po_line_item ASC, e.employee ASC, e.date ASC, e.task ASC, s.charge_number ASC
        <sql:param value="${param.invoice}"/>
    </sql:query>

    <c:forEach var="row" items="${expenseResultSet.rows}">
        <c:set var="expensesTotal" value="${expensesTotal + row.cost}" scope="request"/>
    </c:forEach>

    <c:set var="invoiceTotal" value="${expensesTotal + laborTotal}" scope="request"/>

    <p>Total this invoice: <fmt:formatNumber type="currency" value="${invoiceTotal}"/></p>

    <div>
        <table class="altrows" id="entries">
            <caption>Timesheet Entries</caption>
            <thead>
                <tr>
                    <th>Employee</th>
                    <th>Task</th>
                    <th>Date</th>
                    <th>Rate</th>
                    <th>Line Item</th>
                    <th>Labor Category</th>
                    <th>Charge Number</th>
                    <th>Duration</th>
                    <th>Cost</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="row" items="${result.rows}" varStatus="varStatus">
                    <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                    <tr class="${varStatus.index % 2 == 0 ? 'evenrow' : 'oddrow'}">
                        <td>${directory.byNumber[row.employee].fullName}</td>
                        <td><a href="task.jsp?task_id=${row.task}">${row.task}</a></td>
                        <td>${row.date}</td>
                        <c:choose>
                            <c:when test="${empty costData.laborCategory}">
                                <td class="error">no rate</td>
                                <c:set var="errorsOccurred" value="true"/>
                            </c:when>
                            <c:otherwise>
                                <td class="currency"><fmt:formatNumber type="currency" value="${costData.rate}"/></td>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${!empty row.po_line_item}">
                                <td class="line_item">${row.po_line_item}</td>
                            </c:when>
                            <c:otherwise>
                                <td class="error">No line item!</td>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${empty costData.laborCategory}">
                                <td class="error">no labor category</td>
                                <c:set var="errorsOccurred" value="true"/>
                            </c:when>
                            <c:otherwise>
                                <td>${costData.laborCategory.name}</td>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${!empty row.charge_number}">
                                <td>${row.charge_number}</td>
                            </c:when>
                            <c:otherwise>
                                <td class="error">No charge number!</td>
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
                <tr>
                    <td colspan="8"><strong>Total</strong></td>
                    <td class="currency"><strong><fmt:formatNumber type="currency" value="${laborTotal}"/></strong></td>
                </tr>
            </tbody>
        </table>
    </div>

    <sql:query dataSource="jdbc/sarariman" var="line_items">
        SELECT DISTINCT s.po_line_item
        FROM invoices AS i
        LEFT JOIN saic_tasks AS s ON i.task = s.task
        WHERE i.id = ?
        <sql:param value="${param.invoice}"/>
    </sql:query>
    <sql:query dataSource="jdbc/sarariman" var="employees">
        SELECT DISTINCT h.employee
        FROM invoices AS i
        JOIN tasks AS t ON i.task = t.id
        JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
        WHERE i.id = ? AND t.billable = TRUE
        <sql:param value="${param.invoice}"/>
    </sql:query>
    <div>
        <table class="altrows">
            <caption>Totals by Line Item and Employee</caption>
            <thead>
                <tr>
                    <th>Line Item</th>
                    <th>Employee</th>
                    <th>Total</th>
                </tr>
            </thead>
            <tbody>
                <c:set var="index" value="0"/>
                <c:forEach var="line_item_row" items="${line_items.rows}">
                    <c:forEach var="employee_row" items="${employees.rows}">
                        <sql:query dataSource="jdbc/sarariman" var="total">
                            SELECT SUM(h.duration) AS total
                            FROM invoices AS i
                            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                            LEFT JOIN saic_tasks AS s ON i.task = s.task
                            WHERE i.id = ? AND s.po_line_item = ? AND i.employee = ?
                            <sql:param value="${param.invoice}"/>
                            <sql:param value="${line_item_row.po_line_item}"/>
                            <sql:param value="${employee_row.employee}"/>
                        </sql:query>
                        <c:if test="${!empty total.rows[0].total}">
                            <tr class="${index % 2 == 0 ? 'evenrow' : 'oddrow'}">
                                <td class="line_item">${line_item_row.po_line_item}</td>
                                <td>${directory.byNumber[employee_row.employee].fullName}</td>
                                <td class="duration">${total.rows[0].total}</td>
                            </tr>
                            <c:set var="index" value="${index + 1}"/>
                        </c:if>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <c:if test="${expenseResultSet.rowCount > 0}">
        <div>
            <table class="altrows">
                <caption>Expenses</caption>
                <thead>
                    <tr>
                        <th>Line Item</th>
                        <th>Date</th>
                        <th>Employee</th>
                        <th>Description</th>
                        <th>Cost</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${expenseResultSet.rows}" varStatus="varStatus">
                        <tr class="${varStatus.index % 2 == 0 ? 'evenrow' : 'oddrow'}">
                            <td class="line_item">${row.po_line_item}</td>
                            <td>${row.date}</td>
                            <td>${directory.byNumber[row.employee].fullName}</td>
                            <td>${fn:escapeXml(row.description)}</td>
                            <td class="currency"><fmt:formatNumber type="currency" value="${row.cost}"/></td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="4"><strong>Total</strong></td>
                        <td class="currency"><strong><fmt:formatNumber type="currency" value="${expensesTotal}"/></strong></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </c:if>

    <c:set var="laborCSVLink" value="saic/laborcosts.csv?id=${param.invoice}" scope="request"/>
    <c:set var="expenseCSVLink" value="saic/expenses.csv?id=${param.invoice}" scope="request"/>

    <%
            documentNames.add(String.format("laborcosts-%s.csv", request.getParameter("invoice")));
            documentLinks.add(String.format("saic/laborcosts.csv?id=%s", request.getParameter("invoice")));

            documentNames.add(String.format("expenses-%s.csv", request.getParameter("invoice")));
            documentLinks.add(String.format("saic/expenses.csv?id=%s", request.getParameter("invoice")));
    %>
</c:if>