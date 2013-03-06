<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Deduct PTO</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>

    <c:choose>
        <c:when test="${!user.administrator}">
            <p>You are not authorized for this.</p>
        </c:when>
        <c:otherwise>
            <!-- FIXME: Redirect if date is not a Saturday? -->
            <fmt:parseDate var="parsedWeek" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            <c:set var="week" value="${du:week(parsedWeek)}"/>

            <body onload="altRows()">
                <%@include file="header.jsp" %>

                <h1>Deduct PTO for the week of ${week.name}</h1>

                <table class="altrows" id="timesheets">
                    <tr><th>Employee</th><th>PTO</th></tr>
                    <c:forEach var="employeeEntry" items="${directory.byUserName}">
                        <c:set var="employee" value="${employeeEntry.value}"/>
                        <c:set var="timesheet" value="${sarariman:timesheet(sarariman, employee.number, week)}"/>
                        <c:set var="PTO" value="${timesheet.PTOHours}"/>
                        <c:if test="${PTO gt 0}">
                            <tr>
                                <td>${employee.fullName}</td>
                                <td class="duration"><fmt:formatNumber value="${PTO}" minFractionDigits="2"/></td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </table>

                <form action="DeductPTOHandler" method="POST">
                    <input type="hidden" name="week" value="${week.name}"/>
                    <c:forEach var="employeeEntry" items="${directory.byUserName}">
                        <c:set var="employee" value="${employeeEntry.value}"/>
                        <c:set var="timesheet" value="${sarariman:timesheet(sarariman, employee.number, week)}"/>
                        <c:set var="PTO" value="${timesheet.PTOHours}"/>
                        <c:if test="${PTO gt 0}">
                            <input type="hidden" name="employee" value="${employee.number}"/>
                            <input type="hidden" name="PTO" value="${PTO}"/>
                        </c:if>
                    </c:forEach>
                    <input type="submit" value="Deduct"/> <!-- FIXME: Need a way to disable this when the week has already been done. -->
                </form>

                <%@include file="footer.jsp" %>
            </c:otherwise>
        </c:choose>
    </body>
</html>
