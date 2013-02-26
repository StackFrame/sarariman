<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="com.sun.imageio.plugins.common.I18N"%>
<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@page import="com.google.common.base.Predicate" %>
<%@page import="com.google.common.collect.Iterables" %>
<%@page import="com.google.common.collect.Lists"%>
<%@page import="com.google.common.collect.Range" %>
<%@page import="com.google.common.collect.Ranges"%>
<%@page import="com.stackframe.sarariman.AccessControlUtilities" %>
<%@page import="com.stackframe.sarariman.Directory" %>
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
    final Directory directory = (Directory)request.getAttribute("directory");
    Calendar beginning = Calendar.getInstance();
    int days = 7;
    beginning.add(Calendar.DAY_OF_MONTH, -days);
    Range<Date> dateRange = Ranges.greaterThan(beginning.getTime());
    Iterable<TimesheetEntry> entries = sarariman.getTimesheetEntries().getEntries(dateRange);

    Predicate<TimesheetEntry> visible = new Predicate<TimesheetEntry>() {
        public boolean apply(TimesheetEntry entry) {
            if (entry.getEmployee() == user.getNumber()) {
                return false;
            }

            // FIXME: Consider allowing PTO into the view.

            return AccessControlUtilities.entryVisibleToUser(sarariman.getDataSource(), entry, user, sarariman.getOrganizationHierarchy(), directory);
        }

    };

    entries = Iterables.filter(entries, visible);
    pageContext.setAttribute("entries", Lists.newArrayList(entries));
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Timesheet Activity</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Timesheet Activity</h1>
        <table id="activity" class="altrows">
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
                    <td class="date"><fmt:formatDate value="${entry.date}" pattern="E, MMM d"/></td>
                    <td>${directory.byNumber[entry.employee].displayName}</td>
                    <td class="duration">${fn:escapeXml(entry.duration)}</td>
                    <jsp:useBean id="taskFinder" class="com.stackframe.sarariman.tasks.TaskFinder">
                        <jsp:setProperty name="taskFinder" property="dataSource" value="${sarariman.dataSource}"/>
                    </jsp:useBean>
                    <jsp:setProperty name="taskFinder" property="id" value="${entry.task}"/>
                    <c:set var="task" value="${taskFinder.task}"/>
                    <c:url var="taskURL" value="task">
                        <c:param name="task_id" value="${entry.task}"/>
                    </c:url>
                    <c:set var="project" value="${task.project}"/>
                    <c:choose>
                        <c:when test="${not empty project}">
                            <td>
                                <a href="${taskURL}">${fn:escapeXml(task.name)}</a>
                            </td>
                            <td>
                                <c:url var="projectURL" value="project">
                                    <c:param name="id" value="${project.id}"/>
                                </c:url>
                                <a href="${projectURL}">${fn:escapeXml(project.name)}</a>
                            </td>
                            <td>
                                <c:url var="clientURL" value="customer">
                                    <c:param name="id" value="${project.client.id}"/>
                                </c:url>
                                <a href="${clientURL}">${fn:escapeXml(project.client.name)}</a>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td colspan="3">
                                <a href="${taskURL}">${fn:escapeXml(task.name)}</a>
                            </td>
                        </c:otherwise>
                    </c:choose>
                    <td>${entry.description}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
