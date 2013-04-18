<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
    <fmt:parseNumber var="project_id" value="${param.project}"/>
    <c:set var="project" value="${sarariman.projects.map[project_id]}"/>
    <c:set var="customer" value="${project.client}"/>
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <style type="text/css">
            @media screen, print {
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

                table.timereport {
                    border-width: 1px;
                    border-style: solid;
                    border-collapse: collapse;
                }

                table.timereport td, table.timereport th {
                    border-width: 1px;
                    border-style: solid;
                    padding: 3px;
                }

                th {
                    background: #004d91;
                    color: white;
                    border-color: black;
                }
            }

            @media print {
                a {
                    color: #000;
                    text-decoration: none;
                }

                .dates {
                    white-space: nowrap;
                }

                #topnav {
                    display: none;
                }

                .edit {
                    display: none;
                }

                table.timereport {
                    font-size: 12px;
                    text-align: center;
                }

                table.timereport  td, table.timereport th {
                    padding: 5px;
                }
            }

            @page { size: letter; }
        </style>
        <title>${directory.byNumber[param.employee].fullName} - ${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)} - ${param.week}</title>
    </head>

    <body>
        <%@include file="header.jsp" %>

        <img style="float:right" src="${sarariman.logoURL}"/>
        <h1>Timesheet</h1>
        <p>
            Employee: ${directory.byNumber[param.employee].fullName} <img width="25" height="25" onerror="this.style.display='none'" src="${directory.byNumber[param.employee].photoURL}"/><br/>
            Week: ${param.week}<br/>
            Project: ${fn:escapeXml(project.name)}<br/>
            Client: ${fn:escapeXml(customer.name)}
        </p>

        <fmt:parseDate var="startDay" value="${param.week}" pattern="yyyy-MM-dd" />
        <c:set var="week" value="${du:week(startDay)}"/>
        <c:set var="timesheet" value="${sarariman.timesheets.map[directory.byNumber[param.employee]][week]}"/>
        <c:if test="${!timesheet.approved}">
            <p class="error">This timesheet is not yet approved.</p>
        </c:if>

        <c:set var="timereportTableEmitted" value="${false}" scope="request"/>
        <c:forEach var="extension" items="${sarariman.extensions}">
            <jsp:include page="${extension.timeReportInclude}">
                <jsp:param name="project" value="${param.project}"/>
                <jsp:param name="week" value="${param.week}"/>
                <jsp:param name="employee" value="${param.employee}"/>
                <jsp:param name="startDay" value="${startDay}"/>
            </jsp:include>
        </c:forEach>

        <c:if test="${!timereportTableEmitted}">
            <sql:query dataSource="jdbc/sarariman" var="tasks">
                SELECT DISTINCT h.task, t.name
                FROM hours as h
                JOIN tasks AS t ON h.task = t.id
                JOIN projects AS p ON t.project = p.id
                WHERE h.employee=? AND p.id = ? AND h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY)
                ORDER BY t.id ASC
                <sql:param value="${param.employee}"/>
                <sql:param value="${param.project}"/>
                <sql:param value="${param.week}"/>
                <sql:param value="${param.week}"/>
            </sql:query>
            <table class="timereport">
                <tr>
                    <th>Task</th>
                    <th>Name</th>
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
                        <tr>
                            <td>${task.task}</td>
                            <td>${fn:escapeXml(task.name)}</td>
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
                            <td class="duration"><strong>${rowTotal}</strong></td>
                        </tr>
                    </c:if>
                </c:forEach>
                <tr>
                    <td colspan="2"><strong>Total</strong></td>
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
                                <td class="duration"><strong>0.00</strong></td>
                            </c:when>
                            <c:otherwise>
                                <td class="duration"><strong>${total.rows[0].total}</strong></td>
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
                    <td class="duration"><strong>${total.rows[0].total}</strong></td>
                </tr>
            </table>
        </c:if>

        <!-- FIXME: Add employee picture. -->
        <!-- FIXME: Add a table that summarizes by task. -->

        <sql:query dataSource="jdbc/sarariman" var="detail">
            SELECT h.task, h.date, t.name, h.description, h.employee
            FROM hours as h
            JOIN tasks AS t ON h.task = t.id
            JOIN projects AS p ON t.project = p.id
            WHERE h.employee=? AND h.date >= ? AND h.date < ? AND p.id = ? AND h.duration > 0
            ORDER BY h.date ASC
            <sql:param value="${param.employee}"/>
            <sql:param value="${startDay}"/>
            <sql:param value="${du:addDays(startDay, 7)}"/>
            <sql:param value="${param.project}"/>
        </sql:query>

        <c:forEach var="entry" items="${detail.rows}">
            <h2>${entry.date} - ${entry.task} - ${fn:escapeXml(entry.name)}</h2>
            <div>
                <c:choose>
                    <c:when test="${!sarariman:containsHTML(entry.description)}">
                        <p>${fn:escapeXml(entry.description)}</p>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test='${fn:contains(entry.description, "<p>")}'>
                                ${entry.description}
                            </c:when>
                            <c:otherwise>
                                <p>${entry.description}</p>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
                <c:url var="editLink" value="editentry">
                    <c:param name="employee" value="${entry.employee}"/>
                    <c:param name="date" value="${entry.date}"/>
                    <c:param name="task" value="${entry.task}"/>
                </c:url>
                <a class="edit" href="${fn:escapeXml(editLink)}">Edit</a>
            </div>
        </c:forEach>

        <c:if test="${timesheet.approved}">
            <p>Approved by ${timesheet.approver.fullName} at ${timesheet.approvedTimestamp}.</p>
        </c:if>
        <p>Submitted at ${timesheet.submittedTimestamp}.</p>

        <%@include file="footer.jsp" %>
    </body>
</html>
