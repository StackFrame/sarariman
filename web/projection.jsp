<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="com.stackframe.sarariman.projects.Project"%>
<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@page import="com.stackframe.sarariman.Sarariman"%>
<%@page import="com.stackframe.sarariman.Workdays"%>
<%@page import="com.stackframe.sarariman.PeriodOfPerformance"%>
<%@page import="com.stackframe.sarariman.projects.ProjectedExpense"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Date"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<fmt:parseNumber var="project_id" value="${param.project}"/>
<c:set var="project" value="${sarariman.projects.map[project_id]}"/>
<c:set var="isCostManager" value="${sarariman:isCostManager(user, project)}"/>

<c:if test="${!isCostManager}">
    <jsp:forward page="unauthorized"/>
</c:if>

<c:choose>
    <c:when test="${empty param.start}">
        <c:set var="start" value="${du:now()}"/>
    </c:when>
    <c:otherwise>
        <fmt:parseDate var="start" value="${param.start}" pattern="yyyy-MM-dd"/>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${empty param.end}">
        <c:set var="end" value="${project.poP.end}"/>
    </c:when>
    <c:otherwise>
        <fmt:parseDate var="end" value="${param.end}" pattern="yyyy-MM-dd"/>
    </c:otherwise>
</c:choose>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <title>Projection for ${fn:escapeXml(project.name)}</title>

        <!-- jQuery -->
        <link type="text/css" href="jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />	
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script>
            $(function() {
                $(".hasDatePicker").datepicker({dateFormat: 'yy-mm-dd'});
            });
            
            $(function() {
                $("#parameters").change(function() {
                    this.submit();
                });
            });
        </script>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Projection for <a href="${project.URL}">${fn:escapeXml(project.name)}</a></h1>

        <form id="parameters">
            <label for="start">Start:</label>
            <fmt:formatDate var="startFormatted" value="${start}" type="date" pattern="yyyy-MM-dd" />
            <input type="text" name="start" id="start" value="${startFormatted}" class="hasDatePicker"/>

            <label for="end">End:</label>
            <fmt:formatDate var="endFormatted" value="${end}" type="date" pattern="yyyy-MM-dd" />
            <input type="text" name="end" id="end" value="${endFormatted}" class="hasDatePicker"/>

            <input type="hidden" name="project" value="${param.project}"/>

            <input type="submit" value="Submit"/>
        </form>

        <table>
            <caption>Labor Projections</caption>
            <tr>
                <th>Employee</th>
                <th>Task</th>
                <th>Start</th>
                <th>End</th>
                <th>Utilization</th>
            </tr>
            <c:forEach var="p" items="${project.laborProjections}">
                <tr>
                    <td><a href="${p.employee.URL}">${p.employee.fullName}</a></td>
                    <td><a href="${p.task.URL}">${p.task.id}</a></td>
                    <td>${p.periodOfPerformance.start}</td>
                    <td>${p.periodOfPerformance.end}</td>
                    <td>${p.utilization}</td>
                </tr>
            </c:forEach>
        </table>

        <%
            Date start = (Date)pageContext.getAttribute("start");
            Date end = (Date)pageContext.getAttribute("end");
            Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
            Workdays workdays = sarariman.getWorkdays();
            PeriodOfPerformance pop = new PeriodOfPerformance(start, end);
            Collection<Date> workingDays = workdays.getWorkdays(pop);
            pageContext.setAttribute("workingDays", workingDays);
        %>

        <p>
            Working days in projected period (total days minus weekends and holidays): ${fn:length(workingDays)}
        </p>

        <%
            Project project = (Project)pageContext.getAttribute("project");
            Collection<ProjectedExpense> projectedExpenses = project.getProjectedExpenses(pop);
            pageContext.setAttribute("projectedExpenses", projectedExpenses);
        %>

        <table>
            <caption>Projected Expenses</caption>
            <tr>
                <th>Employee</th>
                <th>Task</th>
                <th>Start</th>
                <th>End</th>
                <th>Hours</th>
            </tr>
            <c:forEach var="e" items="${projectedExpenses}">
                <tr>
                    <td><a href="${e.employee.URL}">${e.employee.fullName}</a></td>
                    <td><a href="${e.task.URL}">${e.task.id}</a></td>
                    <td><fmt:formatDate value="${e.periodOfPerformance.start}" type="date" pattern="yyyy-MM-dd" /></td>
                    <td><fmt:formatDate value="${e.periodOfPerformance.end}" type="date" pattern="yyyy-MM-dd" /></td>
                    <td>${e.hours}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
