<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@page import="com.google.common.collect.Range" %>
<%@page import="com.google.common.collect.Ranges"%>
<%@page import="com.stackframe.sarariman.Sarariman" %>
<%@page import="com.stackframe.sarariman.TimesheetEntry" %>
<%@page import="java.util.Calendar" %>
<%@page import="java.util.Date" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    Calendar beginning = Calendar.getInstance();
    int days = 7;
    beginning.add(Calendar.DAY_OF_MONTH, -days);
    Range<Date> dateRange = Ranges.greaterThan(beginning.getTime());
    Iterable<TimesheetEntry> entries = sarariman.getTimesheetEntries().getEntries(dateRange);
    pageContext.setAttribute("entries", entries);
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
                <th>Description</th>
                <th>Duration</th>
            </tr>
            <c:forEach var="entry" items="${entries}">
                <tr>
                    <td>${entry.date}</td>
                    <td>${directory.byNumber[entry.employee].displayName}</td>
                    <td>${entry.description}</td>
                    <td>${fn:escapeXml(entry.duration)}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
