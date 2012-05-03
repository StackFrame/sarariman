<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Collection,com.stackframe.sarariman.Sarariman,com.stackframe.sarariman.Employee"%>
<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Timesheets</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->
    <!-- FIXME: Need to make PTO stand out for easier payroll processing. -->
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

        <!-- FIXME: This is lame and could be made better with a date picker. -->

        <form action="${request.requestURI}" method="get">
            <fmt:formatDate var="prevWeekString" value="${du:prevWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${prevWeekString}"/>
            <fmt:formatDate var="nextWeekString" value="${du:nextWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${nextWeekString}"/>
            <c:if test="${param.showInactive == 'on'}"><input type="hidden" name="showInactive" value="on"/></c:if>
            </form>

            <form action="${request.requestURI}" method="get">
            <input type="hidden" name="week" value="${param.week}"/>
            <label for="showInactive">Show Inactive</label>
            <input type="checkbox" name="showInactive" id="showInactive" <c:if test="${param.showInactive == 'on'}">checked="checked"</c:if> /><input type="submit" value="Update"/>
            </form>

        <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

        <h2>Timesheets for the week of ${thisWeekStart}</h2>

        <%
            Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
            Employee user = (Employee)request.getAttribute("user");
            System.err.println("user="+user);
            Collection<Integer> reports = sarariman.getOrganizationHierarchy().getReports(user.getNumber());
            System.err.println("reports="+reports);
            pageContext.setAttribute("reports", reports);
        %>

        <table class="altrows" id="timesheets">
            <tr><th>Employee</th><th>Regular</th><th>PTO</th><th>Holiday</th><th>Total</th><th>Approved</th><th>Submitted</th></tr>
            <c:forEach var="employeeEntry" items="${directory.byUserName}">
                <c:set var="employee" value="${employeeEntry.value}"/>
                <c:if test="${(employee.active || param.showInactive == 'on') && sarariman:contains(reports, employee.number)}">
                    <tr>
                        <c:set var="timesheet" value="${sarariman:timesheet(sarariman, employee.number, week)}"/>
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
                        <td class="checkbox">
                            <form>
                                <input type="checkbox" name="approved" id="approved" disabled="true" <c:if test="${approved}">checked="checked"</c:if>/>
                                </form>
                            </td>
                            <td class="checkbox">
                                <form>
                                    <input type="checkbox" name="submitted" id="submitted" disabled="true" <c:if test="${submitted}">checked="checked"</c:if>/>
                                </form>
                            </td>
                        </tr>
                </c:if>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
