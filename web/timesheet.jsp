<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <c:choose>
        <c:when test="${!empty param.employee}">
            <c:set var="employee" value="${directory.byNumber[param.employee]}"/>
        </c:when>
        <c:otherwise>
            <c:set var="employee" value="${user}"/>
        </c:otherwise>
    </c:choose>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <style type="text/css">
            @media print{
                #topnav {
                    display: none;
                }
            }
        </style>
        <title>${employee.fullName}</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <c:choose>
            <c:when test="${!empty param.week}">
                <fmt:parseDate var="week" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            </c:when>
            <c:otherwise>
                <c:set var="week" value="${du:weekStart(du:now())}"/>
            </c:otherwise>
        </c:choose>

        <form action="${request.requestURI}" method="get">
            <fmt:formatDate var="prevWeekString" value="${du:prevWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${prevWeekString}"/>
            <fmt:formatDate var="nextWeekString" value="${du:nextWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${nextWeekString}"/>
            <input type="hidden" name="employee" value="${employee.number}"/>
        </form>
        <form>
            <label for="employee">Employee: </label>
            <select name="employee" id="employee">
                <c:forEach var="e" items="${directory.byUserName}">
                    <option value="${e.value.number}" <c:if test="${e.value.number == employee.number}">selected="selected"</c:if>>${e.value.fullName}</option>
                </c:forEach>
            </select>
            <fmt:formatDate var="weekString" value="${week}" pattern="yyyy-MM-dd"/>
            <input type="hidden" name="week" value="${weekString}"/>
            <input type="submit" value="Retrieve"/>
        </form>

        <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

        <c:set var="timesheet" value="${sarariman:timesheet(sarariman, employee.number, week)}"/>
        <c:if test="${user.administrator}">
            <form method="post" action="timesheetController">
                <input type="hidden" value="${thisWeekStart}" name="week"/>
                <input type="hidden" value="${employee.number}" name="employee"/>
                <c:if test="${!timesheet.approved}">
                    <input type="submit" name="action" value="Approve" <c:if test="${!timesheet.submitted}">disabled="disabled"</c:if>/>
                </c:if>
                <!-- FIXME: Only allow this if the time has not been invoiced. -->
                <input type="submit" name="action" value="Reject"  <c:if test="${!timesheet.submitted}">disabled="disabled"</c:if>/>
            </form>
        </c:if>

        <h2>Timesheet for ${employee.fullName} for the week of ${thisWeekStart}</h2>
        <c:if test="${!timesheet.approved}">
            <p class="error">This timesheet is not yet approved.</p>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="entries">
            SELECT hours.task, hours.description, hours.date, hours.duration, tasks.name, tasks.project, tasks.billable
            FROM hours
            INNER JOIN tasks ON hours.task=tasks.id
            WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY)
            ORDER BY hours.date DESC, hours.task ASC
            <sql:param value="${employee.number}"/>
            <sql:param value="${thisWeekStart}"/>
            <sql:param value="${thisWeekStart}"/>
        </sql:query>
        <c:set var="totalRegular" value="0.0"/>
        <c:set var="totalHoursWorked" value="0.0"/>
        <c:set var="totalUnbillable" value="0.0"/>
        <c:set var="totalPTO" value="0.0"/>
        <c:set var="totalHoliday" value="0.0"/>
        <c:set var="projects" value="${sarariman.projects}"/>
        <table class="altrows" id="timesheet">
            <tr><th>Date</th><th>Task</th><th>Task #</th><th>Project</th><th>Customer</th><th>Duration</th><th>Description</th></tr>
            <c:forEach var="entry" items="${entries.rows}">
                <c:set var="project" value="${projects[entry.project]}"/>
                <c:if test="${user.administrator || (!empty project && sarariman:isManager(user, project))}">
                    <tr>
                        <fmt:formatDate var="entryDate" value="${entry.date}" pattern="E, MMM d"/>
                        <td class="date">${entryDate}</td>
                        <c:url var="taskLink" value="task">
                            <c:param name="task_id" value="${entry.task}"/>
                        </c:url>
                        <td><a href="${fn:escapeXml(taskLink)}">${fn:escapeXml(entry.name)}</a></td>
                        <td class="task"><a href="${fn:escapeXml(taskLink)}">${entry.task}</a></td>
                        <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                        <c:url var="projectLink" value="project">
                            <c:param name="id" value="${entry.project}"/>
                        </c:url>
                        <td>
                            <c:if test="${!empty project}">
                                <a href="${fn:escapeXml(projectLink)}">${fn:escapeXml(project.name)}</a>
                            </c:if>
                        </td>
                        <c:url var="customerLink" value="customer">
                            <c:param name="id" value="${project.customer}"/>
                        </c:url>
                        <td>
                            <c:if test="${!empty customer}">
                                <a href="${fn:escapeXml(customerLink)}">${fn:escapeXml(customer.name)}</a>
                            </c:if>
                        </td>

                        <c:choose>
                            <%-- FIXME: This needs to look this up somewhere. --%>
                            <c:when test="${entry.task == 5}">
                                <c:set var="totalPTO" value="${totalPTO + entry.duration}"/>
                            </c:when>
                            <c:when test="${entry.task == 4}">
                                <c:set var="totalHoliday" value="${totalHoliday + entry.duration}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="totalRegular" value="${totalRegular + entry.duration}"/>
                                <c:if test="${!entry.billable}">
                                    <c:set var="totalUnbillable" value="${totalUnbillable + entry.duration}"/>
                                </c:if>
                            </c:otherwise>
                        </c:choose>

                        <td class="duration">${entry.duration}</td>
                        <td>${entry.description}</td>
                        <c:set var="totalHoursWorked" value="${totalHoursWorked + entry.duration}"/>
                    </tr>
                </c:if>
            </c:forEach>
            <tr>
                <td colspan="5">Total</td>
                <td class="duration">${totalHoursWorked}</td>
                <td></td>
            </tr>
            <tr>
                <td colspan="5">Total Regular</td>
                <td class="duration">${totalRegular}</td>
                <td></td>
            </tr>
            <tr>
                <td colspan="5">Total Unbillable</td>
                <td class="duration">${totalUnbillable}</td>
                <td></td>
            </tr>
            <tr>
                <td colspan="5">Total PTO</td>
                <td class="duration">${totalPTO}</td>
                <td></td>
            </tr>
            <tr>
                <td colspan="5">Total Holiday</td>
                <td class="duration">${totalHoliday}</td>
                <td></td>
            </tr>
        </table>

        <c:if test="${totalHoursWorked > 40.0 && totalPTO > 0.0}">
            <p class="error">PTO taken when sheet is above 40 hours!</p>
        </c:if>

        <c:if test="${timesheet.approved}">
            <p>Approved by ${timesheet.approver.fullName} at ${timesheet.approvedTimestamp}.</p>
        </c:if>
        <c:if test="${!empty timesheet.submittedTimestamp}">
            <p>Submitted at ${timesheet.submittedTimestamp}.</p>
        </c:if>

        <%@include file="footer.jsp" %>
    </body>
</html>
