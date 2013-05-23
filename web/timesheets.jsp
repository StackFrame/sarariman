<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Collection"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="com.stackframe.sarariman.Directory"%>
<%@page import="com.stackframe.sarariman.Employee"%>
<%@page import="com.stackframe.sarariman.projects.Project"%>
<%@page import="com.stackframe.sarariman.Sarariman"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Timesheets</title>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->
    <!-- FIXME: Need to make PTO stand out for easier payroll processing. -->
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <c:choose>
                <c:when test="${!empty param.week}">
                    <fmt:parseDate var="parsedWeek" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
                    <c:set var="week" value="${du:week(parsedWeek)}"/>
                </c:when>
                <c:otherwise>
                    <c:set var="week" value="${du:week(du:now())}"/>
                </c:otherwise>
            </c:choose>

            <!-- FIXME: This is lame and could be made better with a date picker. -->

            <form action="${request.requestURI}" method="get">
                <fmt:formatDate var="prevWeekString" value="${week.previous.start.time}" type="date" pattern="yyyy-MM-dd"/>
                <input class="btn" type="submit" name="week" value="${prevWeekString}"/>
                <fmt:formatDate var="nextWeekString" value="${week.next.start.time}" type="date" pattern="yyyy-MM-dd"/>
                <input class="btn" type="submit" name="week" value="${nextWeekString}"/>
                <c:if test="${param.showInactive == 'on'}"><input type="hidden" name="showInactive" value="on"/></c:if>
            </form>

            <form action="${request.requestURI}" method="get">
                <input type="hidden" name="week" value="${param.week}"/>
                <label for="showInactive">Show Inactive</label>
                <input type="checkbox" name="showInactive" id="showInactive"
                       <c:if test="${param.showInactive == 'on'}">checked="checked"</c:if> />
                <input class="btn" type="submit" value="Update"/>
            </form>

            <fmt:formatDate var="thisWeekStart" value="${week.start.time}" type="date" pattern="yyyy-MM-dd" />

            <c:if test="${user.administrator}">
                <c:url var="deductPTOLink" value="deductPTO.jsp">
                    <c:param name="week" value="${thisWeekStart}"/>
                </c:url>
                <a href="${deductPTOLink}">Deduct PTO</a>
            </c:if>

            <h2>Timesheets for the week of ${thisWeekStart}</h2>

            <%
                Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
                Employee user = (Employee)request.getAttribute("user");
                String showInactiveParam = request.getParameter("showInactive");
                boolean showInactive = showInactiveParam != null && showInactiveParam.equals("on");
                Collection<Integer> reports = sarariman.getOrganizationHierarchy().getReports(user.getNumber(), showInactive);
                pageContext.setAttribute("reports", reports);

                Set<Project> projectsAssisting = user.getProjectsAdministrativelyAssisting();
                Set<Employee> administrativelyAssisting = new HashSet<Employee>();
                pageContext.setAttribute("administrativelyAssisting", administrativelyAssisting);
                for (Project project : projectsAssisting) {
                    administrativelyAssisting.addAll(project.getCurrentlyAssigned());
                }
            %>

            <table id="timesheets" class="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Employee</th>
                        <th>Regular</th>
                        <th>PTO</th>
                        <th>Unpaid Leave</th>
                        <th>Paid</th>
                        <th>Approved</th>
                        <th>Submitted</th>
                        <th>On Time</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="employeeEntry" items="${directory.byUserName}">
                        <c:set var="employee" value="${employeeEntry.value}"/>
                        <c:if test="${(employee.active || param.showInactive == 'on') &&
                                      (sarariman:contains(reports, employee.number) or
                                      sarariman:contains(administrativelyAssisting, employee))}">
                              <tr>
                                  <c:set var="timesheet" value="${sarariman.timesheets.map[employee][week]}"/>
                                  <c:set var="PTO" value="${timesheet.PTOHours}"/>
                                  <c:set var="holiday" value="${timesheet.holidayHours}"/>
                                  <c:set var="unpaidLeave" value="${timesheet.unpaidLeaveHours}"/>
                                  <c:set var="hours" value="${timesheet.totalHours}"/>
                                  <td>
                                      <c:url var="timesheetLink" value="timesheet">
                                          <c:param name="employee" value="${employee.number}"/>
                                          <c:param name="week" value="${thisWeekStart}"/>
                                      </c:url>
                                      <a href="${fn:escapeXml(timesheetLink)}">${employee.fullName}</a>
                                  </td>
                                  <td class="duration"><fmt:formatNumber value="${hours - (PTO + holiday + unpaidLeave)}"
                                                    minFractionDigits="2"/></td>
                                  <td class="duration"><fmt:formatNumber value="${PTO + holiday}" minFractionDigits="2"/></td>
                                  <td class="duration"><fmt:formatNumber value="${unpaidLeave}" minFractionDigits="2"/></td>
                                  <td class="duration"><fmt:formatNumber value="${hours - unpaidLeave}" minFractionDigits="2"/></td>
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
                                  <td style="text-align: center">
                                      <c:choose>
                                          <c:when test="${approved}"><i class="icon-check"></i></c:when>
                                          <c:otherwise><i class="icon-check-empty"></i></c:otherwise>
                                      </c:choose>
                                  </td>
                                  <td style="text-align: center">
                                      <c:choose>
                                          <c:when test="${submitted}"><i class="text-center icon-check"></i></c:when>
                                          <c:otherwise><i class="text-center icon-check-empty"></i></c:otherwise>
                                      </c:choose>
                                  </td>

                                  <sql:query dataSource="jdbc/sarariman" var="averageEntry">
                                      SELECT AVG(DATEDIFF(hours_changelog.timestamp, hours.date)) AS average
                                      FROM hours
                                      JOIN hours_changelog ON hours.employee = hours_changelog.employee AND
                                      hours.task = hours_changelog.task AND hours.date = hours_changelog.date
                                      WHERE hours.employee = ? AND hours.date >= ?
                                      <sql:param value="${employee.number}"/>
                                      <sql:param value="${thisWeekStart}"/>
                                  </sql:query>
                                  <c:set var="good" value="${empty averageEntry.rows[0].average ||
                                                             averageEntry.rows[0].average < 0.25}"/>
                                  <td style="text-align: center">
                                      <c:choose>
                                          <c:when test="${good}">&#x263A;</c:when>
                                          <c:otherwise>&#x2639;</c:otherwise>
                                      </c:choose>
                                  </td>

                              </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
