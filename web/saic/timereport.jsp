<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sql:query dataSource="jdbc/sarariman" var="customerResult">
    SELECT p.customer
    FROM projects as p
    WHERE p.id = ?
    <sql:param value="${param.project}"/>
</sql:query>
<c:set var="isSAIC" value="${customerResult.rows[0].customer == 1}"/>

<c:if test="${isSAIC}">
    <fmt:parseDate var="startDay" value="${param.week}" pattern="yyyy-MM-dd" />
    <sql:query dataSource="jdbc/sarariman" var="tasks">
        SELECT DISTINCT h.task, t.name, s.charge_number, s.po_line_item, s.wbs
        FROM hours as h
        JOIN tasks AS t ON h.task = t.id
        JOIN projects AS p ON t.project = p.id
        LEFT OUTER JOIN saic_tasks AS s ON s.task = t.id
        WHERE h.employee=? AND p.id = ? AND h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY)
        ORDER BY t.id ASC
        <sql:param value="${param.employee}"/>
        <sql:param value="${param.project}"/>
        <sql:param value="${param.week}"/>
        <sql:param value="${param.week}"/>
    </sql:query>
    <c:set var="timereportTableEmitted" value="${true}" scope="request"/>
    <table class="timereport">
        <tr>
            <th>Task</th>
            <th>Name</th>
            <th>Charge Number</th>
            <th>WBS</th>
            <th>Line Item</th>
            <c:forEach var="day" begin="0" end="6">
                <fmt:formatDate var="fmtDay" value="${du:addDays(startDay, day)}" pattern="MM-dd"/>
                <th class="dates">${fmtDay}</th>
            </c:forEach>
            <th>Total</th>
        </tr>
        <c:forEach var="task" items="${tasks.rows}">
            <sql:query dataSource="jdbc/sarariman" var="total">
                SELECT SUM(h.duration) AS total
                FROM hours as h
                WHERE h.employee=? AND h.task = ? AND h.date >= ? AND h.date < ?
                <sql:param value="${param.employee}"/>
                <sql:param value="${task.task}"/>
                <sql:param value="${startDay}"/>
                <sql:param value="${du:addDays(startDay, 7)}"/>
            </sql:query>
            <c:set var="rowTotal" value="${total.rows[0].total}"/>
            <c:if test="${rowTotal > 0}">
                <tr <c:if test="${empty task.charge_number}">class="error"</c:if>>
                    <td>${task.task}</td>
                    <td>${fn:escapeXml(task.name)}</td>
                    <td class="chargeNum">${task.charge_number}</td>
                    <td>${task.wbs}</td>
                    <td>${task.po_line_item}</td>
                    <c:forEach var="day" begin="0" end="6">
                        <c:set var="date" value="${du:addDays(startDay, day)}"/>
                        <sql:query dataSource="jdbc/sarariman" var="duration">
                            SELECT h.duration
                            FROM hours as h
                            WHERE h.employee=? AND h.date = ? AND h.task = ?
                            <sql:param value="${param.employee}"/>
                            <sql:param value="${date}"/>
                            <sql:param value="${task.task}"/>
                        </sql:query>
                        <c:choose>
                            <c:when test="${duration.rowCount == 0}">
                                <td class="duration">0.00</td>
                            </c:when>
                            <c:otherwise>
                                <td class="duration">${duration.rows[0].duration}</td>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <th class="duration">${rowTotal}</th>
                </tr>
            </c:if>
        </c:forEach>
        <tr>
            <th colspan="5">Total</th>
            <c:forEach var="day" begin="0" end="6">
                <c:set var="date" value="${du:addDays(startDay, day)}"/>
                <sql:query dataSource="jdbc/sarariman" var="total">
                    SELECT SUM(h.duration) AS total
                    FROM hours as h
                    JOIN tasks AS t ON h.task = t.id
                    JOIN projects AS p ON t.project = p.id
                    WHERE h.employee=? AND h.date = ? AND p.id = ?
                    <sql:param value="${param.employee}"/>
                    <sql:param value="${date}"/>
                    <sql:param value="${param.project}"/>
                </sql:query>
                <c:choose>
                    <c:when test="${total.rows[0].total == null}">
                        <th class="duration">0.00</th>
                    </c:when>
                    <c:otherwise>
                        <th class="duration">${total.rows[0].total}</th>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <sql:query dataSource="jdbc/sarariman" var="total">
                SELECT SUM(h.duration) AS total
                FROM hours as h
                JOIN tasks AS t ON h.task = t.id
                JOIN projects AS p ON t.project = p.id
                WHERE h.employee=? AND h.date >= ? AND h.date < ? AND p.id = ?
                <sql:param value="${param.employee}"/>
                <sql:param value="${startDay}"/>
                <sql:param value="${du:addDays(startDay, 7)}"/>
                <sql:param value="${param.project}"/>
            </sql:query>
            <th class="duration">${total.rows[0].total}</th>
        </tr>
    </table>
</c:if>