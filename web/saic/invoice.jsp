<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%
        Employee user = (Employee)request.getAttribute("user");
        if (!user.isInvoiceManager()) {
            response.sendError(401);
            return;
        }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <style type="text/css">
            @media print{
                #topnav {
                    display: none;
                }
            }
        </style>
        <title>Invoice ${param.invoice}</title>
        <script type="text/javascript" src="../utilities.js"/>
    </head>
    <body  onload="altRows()">
        <%@include file="../header.jsp" %>

        <h1>SAIC Invoice ${param.invoice}</h1>

        <p>SAIC specific details for <a href="../invoice?invoice=${param.invoice}">invoice ${param.invoice}</a>.</p>

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
            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            WHERE i.id = ?
            <sql:param value="${param.invoice}"/>
        </sql:query>
        <table class="altrows">
            <caption>Entries by Line Item and Employee</caption>
            <tbody>
                <tr>
                    <th>Line Item</th>
                    <th>Employee</th>
                    <th>Total</th>
                </tr>
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
                            <tr>
                                <td class="line_item">${line_item_row.po_line_item}</td>
                                <td>${directory.byNumber[employee_row.employee].fullName}</td>
                                <td class="duration">${total.rows[0].total}</td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>

        <p><a href="laborcosts.csv?id=${param.invoice}">Download as CSV</a></p>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT i.employee, i.task, t.project, i.date, h.duration, s.po_line_item, s.charge_number
            FROM invoices AS i
            JOIN hours AS h ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
            JOIN tasks AS t on t.id = i.task
            LEFT JOIN saic_tasks AS s ON i.task = s.task
            WHERE i.id = ?
            ORDER BY s.po_line_item ASC, h.employee ASC, h.date ASC, h.task ASC, s.charge_number ASC
            <sql:param value="${param.invoice}"/>
        </sql:query>
        <table class="altrows" id="entries">
            <caption>Entries</caption>
            <tbody>
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
                <c:set var="projectBillRates" value="${sarariman.projectBillRates}"/>
                <c:set var="laborCategories" value="${sarariman.laborCategories}"/>
                <c:forEach var="row" items="${result.rows}">
                    <c:set var="costData" value="${sarariman:cost(sarariman, laborCategories, projectBillRates, row.project, row.employee, row.date, row.duration)}"/>
                    <tr>
                        <td>${directory.byNumber[row.employee].fullName}</td>
                        <td><a href="task.jsp?task_id=${row.task}">${row.task}</a></td>
                        <td>${row.date}</td>
                        <td class="currency"><fmt:formatNumber type="currency" value="${costData.rate}"/></td>
                        <c:choose>
                            <c:when test="${!empty row.po_line_item}">
                                <td class="line_item">${row.po_line_item}</td>
                            </c:when>
                            <c:otherwise>
                                <td class="error">No line item!</td>
                            </c:otherwise>
                        </c:choose>
                        <td>${costData.laborCategory.name}</td>
                        <c:choose>
                            <c:when test="${!empty row.charge_number}">
                                <td>${row.charge_number}</td>
                            </c:when>
                            <c:otherwise>
                                <td class="error">No charge number!</td>
                            </c:otherwise>
                        </c:choose>
                        <td class="duration">${row.duration}</td>
                        <td class="currency"><fmt:formatNumber type="currency" value="${costData.cost}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <%@include file="../footer.jsp" %>
    </body>
</html>
