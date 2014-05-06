<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.stackframe.sarariman.projects.Project"%>
<%@page import="com.stackframe.sarariman.Sarariman"%>
<%@page import="com.stackframe.sarariman.Workdays"%>
<%@page import="com.stackframe.sarariman.PeriodOfPerformance"%>
<%@page import="com.stackframe.sarariman.projects.ProjectedExpense"%>
<%@page import="com.stackframe.sarariman.projects.ProjectedExpenses"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Date"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Projection for ${fn:escapeXml(project.name)}</title>

        <!-- jQuery -->
        <link type="text/css" href="jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />
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

            $(document).ready(function(){
                $('.delete').click(function(e) {
                    $.ajax({
                        type: 'DELETE',
                        url: $(this).attr('data'),
                        async: false,
                        success: function() {
                            location.reload();
                        }
                    });
                });
            });
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

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
                    <th></th>
                </tr>
                <c:forEach var="p" items="${project.laborProjections}">
                    <tr>
                        <td><a href="${p.employee.URL}">${p.employee.fullName}</a></td>
                        <td><a href="${p.task.URL}">${p.task.id}</a></td>
                        <td class="date">${p.periodOfPerformance.start}</td>
                        <td class="date">${p.periodOfPerformance.end}</td>
                        <td class="percentage"><fmt:formatNumber type="percent" value="${p.utilization}"/></td>
                        <td>
                            <a href="${p.URL}" class="btn" title="edit this labor projection"><i class="icon-edit"></i></a>
                            <span class="btn btn-danger delete" data="${p.URL}" title="delete this labor projection"><i class="icon-trash"></i></span>
                        </td>
                    </tr>
                </c:forEach>
            </table>

            <p>
                <c:url var="createLink" value="${sarariman.laborProjections.URL}create">
                    <c:param name="project" value="${project.id}"/>
                </c:url>
                <a class="btn" href="${createLink}" title="add a labor projection"><i class="icon-plus"></i></a>
            </p>

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
                ProjectedExpenses projectedExpenses = project.getProjectedExpenses();
                pageContext.setAttribute("projectedExpenses", projectedExpenses);
                pageContext.setAttribute("labor", projectedExpenses.getLabor(pop));
            %>

            <table>
                <caption>Projected Expenses</caption>
                <tr>
                    <th>Employee</th>
                    <th>Task</th>
                    <th>Start</th>
                    <th>End</th>
                    <th>Hours</th>
                    <th>Cost</th>
                </tr>
                <c:set var="totalCost" value="0"/>
                <c:forEach var="e" items="${labor}">
                    <tr>
                        <td><a href="${e.employee.URL}">${e.employee.fullName}</a></td>
                        <td><a href="${e.task.URL}">${e.task.id}</a></td>
                        <td class="date"><fmt:formatDate value="${e.periodOfPerformance.start}" type="date" pattern="yyyy-MM-dd" /></td>
                        <td class="date"><fmt:formatDate value="${e.periodOfPerformance.end}" type="date" pattern="yyyy-MM-dd" /></td>
                        <td class="duration"><fmt:formatNumber value="${e.hours}" minFractionDigits="2"/></td>
                        <td class="currency"><fmt:formatNumber type="currency" value="${e.cost}"/></td>
                        <c:set var="totalCost" value="${totalCost + e.cost}"/>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="5"><b>Total</b></td>
                    <td class="currency"><b><fmt:formatNumber type="currency" value="${totalCost}"/></b></td>
                </tr>
            </table>

                <!-- FIXME: Deanna was able to create a projection with a bogus range and then it could not be viewed. We need to return an error on INSERT. -->
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
