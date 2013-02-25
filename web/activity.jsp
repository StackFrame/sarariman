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
<%@page import="com.stackframe.sarariman.Employee" %>
<%@page import="com.stackframe.sarariman.Project" %>
<%@page import="com.stackframe.sarariman.Sarariman" %>
<%@page import="com.stackframe.sarariman.Task" %>
<%@page import="com.stackframe.sarariman.TimesheetEntry" %>
<%@page import="java.sql.SQLException" %>
<%@page import="java.util.Calendar" %>
<%@page import="java.util.Date" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    final Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    final Employee user = (Employee)request.getAttribute("user");
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

            try {
                Task task = Task.getTask(sarariman, entry.getTask());
                Project project = task.getProject();
                if (project == null) {
                    return sarariman.isBoss(user);
                } else {
                    return project.isManager(user) || project.isCostManager(user);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
                <th>Description</th>
            </tr>
            <c:forEach var="entry" items="${entries}">
                <tr>
                    <td>${entry.date}</td>
                    <td>${directory.byNumber[entry.employee].displayName}</td>
                    <td>${fn:escapeXml(entry.duration)}</td>
                    <td>${entry.task}</td>
                    <td>${entry.description}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
