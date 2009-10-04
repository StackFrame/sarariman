<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<jsp:useBean beanName="sarariman" id="sarariman" scope="application" type="com.stackframe.sarariman.Sarariman" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Timesheets</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->
    <!-- FIXME: Need to make PTO stand out for easier payroll processing. -->
    <body onload ="altRows('timesheets')">
        <p><a href="./">Home</a></p>
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
        </form>

        <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

        <c:if test="${sarariman:isAdministrator(user) && !empty param.action}">
            <c:set var="timesheet" value="${sarariman:timesheet(sarariman, db, param.actionEmployee, week)}"/>
            <c:set var="fullName" value="${directory.employeeMap[param.actionEmployee].fullName}"/>
            <c:choose>
                <c:when test="${param.action == 'Approve'}">
                    <c:choose>
                        <c:when test="${sarariman:approveTimesheet(timesheet)}">
                            <p>Approved timesheet for ${fullName} for ${week}.</p>
                        </c:when>
                        <c:otherwise>
                            <p class="error">Failed to approve timesheet for ${fullName} for ${week}.</p>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${param.action == 'Reject'}">
                    <!-- FIXME: Only allow this if the time has not been invoiced. -->
                    <c:choose>
                        <c:when test="${sarariman:rejectTimesheet(timesheet)}">
                            <p>Rejected timesheet for ${fullName} for ${week}.</p>
                        </c:when>
                        <c:otherwise>
                            <p class="error">Failed to reject timesheet for ${fullName} for ${week}.</p>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <p class="error">Impossible action!</p>
                </c:otherwise>
            </c:choose>
        </c:if>

        <h2>Timesheets for the week of ${thisWeekStart}</h2>

        <table class="altrowstable" id="timesheets">
            <tr><th>Employee</th><th>Regular</th><th>PTO</th><th>Holiday</th><th>Total</th><th>Approved</th><th>Submitted</th><th colspan="2">Actions</th></tr>
            <c:forEach var="employee" items="${directory.employees}">
                <tr>
                    <c:set var="timesheet" value="${sarariman:timesheet(sarariman, db, employee.number, week)}"/>
                    <c:set var="PTO" value="${timesheet.PTOHours}"/>
                    <c:set var="holiday" value="${timesheet.holidayHours}"/>
                    <c:set var="hours" value="${timesheet.totalHours}"/>
                    <td>
                        <c:url var="timesheetLink" value="timesheet">
                            <c:param name="employee" value="${employee.number}"/>
                            <c:param name="week" value="${thisWeekStart}"/>
                        </c:url>
                        <a href="${fn:escapeXml(timesheetLink)}">${employee.fullName}</a>
                    </td>
                    <td class="duration"><fmt:formatNumber value="${hours - (PTO + holiday)}" minFractionDigits="2"/></td>
                    <td class="duration"><fmt:formatNumber value="${PTO}" minFractionDigits="2"/></td>
                    <td class="duration"><fmt:formatNumber value="${holiday}" minFractionDigits="2"/></td>
                    <td class="duration"><fmt:formatNumber value="${hours}" minFractionDigits="2"/></td>
                    <c:choose>
                        <c:when test="${!timesheet.submitted}">
                            <c:set var="approved" value="false"/>
                            <c:set var="submitted" value="false"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="approved" value="${timesheet.approved}"/>
                            <c:set var="submitted" value="true"/>
                        </c:otherwise>
                    </c:choose>
                    <td>
                        <form>
                            <input type="checkbox" name="approved" id="approved" disabled="true" <c:if test="${approved}">checked="checked"</c:if>/>
                        </form>
                    </td>
                    <td>
                        <form>
                            <input type="checkbox" name="submitted" id="submitted" disabled="true" <c:if test="${submitted}">checked="checked"</c:if>/>
                        </form>
                    </td>
                    <td>
                        <form method="post">
                            <input type="hidden" value="${thisWeekStart}" name="actionWeek"/>
                            <input type="hidden" value="${employee.number}" name="actionEmployee"/>
                            <c:if test="${sarariman:isAdministrator(user)}">
                                <c:if test="${!approved}">
                                    <input type="submit" name="action" value="Approve" <c:if test="${!submitted}">disabled="disabled"</c:if>/>
                                </c:if>
                                <!-- FIXME: Only allow this if the time has not been invoiced. -->
                                <input type="submit" name="action" value="Reject"  <c:if test="${!submitted}">disabled="disabled"</c:if>/>
                            </c:if>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
