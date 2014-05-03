<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.google.common.base.Predicate" %>
<%@page import="com.google.common.collect.Iterables" %>
<%@page import="com.google.common.collect.Lists"%>
<%@page import="com.google.common.collect.Range" %>
<%@page import="com.stackframe.sarariman.AccessControlUtilities" %>
<%@page import="com.stackframe.sarariman.Employee" %>
<%@page import="com.stackframe.sarariman.Sarariman" %>
<%@page import="com.stackframe.sarariman.TimesheetEntry" %>
<%@page import="java.util.Calendar" %>
<%@page import="java.util.Date" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    final Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    final Employee user = (Employee)request.getAttribute("user");
    Calendar beginning = Calendar.getInstance();
    int days = 7;
    beginning.add(Calendar.DAY_OF_MONTH, -days);
    Range<Date> dateRange = Range.greaterThan(beginning.getTime());
    Iterable<TimesheetEntry> entries = sarariman.getTimesheetEntries().getEntries(dateRange);

    Predicate<TimesheetEntry> visible = new Predicate<TimesheetEntry>() {
        public boolean apply(TimesheetEntry entry) {
            if (entry.getEmployee() == user) {
                return false;
            }

            // FIXME: Consider allowing PTO into the view.

            return AccessControlUtilities.entryVisibleToUser(sarariman.getDataSource(), entry, user, sarariman.getOrganizationHierarchy(), sarariman.getTasks());
        }

    };

    entries = Iterables.filter(entries, visible);
    pageContext.setAttribute("entries", Lists.newArrayList(entries));
%>

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
        <title>Timesheet Activity</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>Timesheet Activity</h1>
            <table id="activity" class="table table-bordered table-rounded table-striped">
                <tr>
                    <th>Date</th>
                    <th>Employee</th>
                    <th>Duration</th>
                    <th>Task</th>
                    <th>Project</th>
                    <th>Client</th>
                    <th>Description</th>
                </tr>
                <c:forEach var="entry" items="${entries}">
                    <tr>
                        <td class="date"><a href="${fn:escapeXml(entry.URL)}"><fmt:formatDate value="${entry.date}" pattern="E, MMM d"/></a></td>
                        <td>
                            <a href="${entry.employee.URL}">${entry.employee.displayName}</a>
                            <a href="${entry.employee.URL}"><img width="25" height="25" onerror="this.style.display='none'" src="${entry.employee.photoURL}"/></a>
                        </td>
                        <td class="duration">${fn:escapeXml(entry.duration)}</td>
                        <c:set var="task" value="${entry.task}"/>
                        <c:set var="project" value="${task.project}"/>
                        <c:choose>
                            <c:when test="${not empty project}">
                                <td><a href="${task.URL}">${fn:escapeXml(task.name)}</a></td>
                                <td><a href="${project.URL}">${fn:escapeXml(project.name)}</a></td>
                                <td><a href="${project.client.URL}">${fn:escapeXml(project.client.name)}</a></td>
                            </c:when>
                            <c:otherwise>
                                <td colspan="3">
                                    <a href="${task.URL}">${fn:escapeXml(task.name)}</a>
                                </td>
                            </c:otherwise>
                        </c:choose>
                        <td>${entry.description}<a class="icon-link" title="go to this entry" href="${fn:escapeXml(entry.URL)}"></a></td>
                    </tr>
                </c:forEach>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
