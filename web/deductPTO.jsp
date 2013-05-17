<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Deduct PTO</title>
    </head>

    <c:choose>
        <c:when test="${!user.administrator}">
            <p>You are not authorized for this.</p>
        </c:when>
        <c:otherwise>
            <!-- FIXME: Redirect if date is not a Saturday? -->
            <fmt:parseDate var="parsedWeek" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            <c:set var="week" value="${du:week(parsedWeek)}"/>

            <body>
                <%@include file="/WEB-INF/jspf/navbar.jspf" %>

                <div class="container-fluid">

                    <h1>Deduct PTO for the week of ${week.name}</h1>

                    <table id="timesheets" class="table table-bordered table-rounded table-striped">
                        <thead>
                            <tr><th>Employee</th><th>PTO</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="employeeEntry" items="${directory.byUserName}">
                                <c:set var="employee" value="${employeeEntry.value}"/>
                                <c:set var="timesheet" value="${sarariman.timesheets.map[employee][week]}"/>
                                <c:set var="PTO" value="${timesheet.PTOHours}"/>
                                <c:if test="${PTO gt 0}">
                                    <tr>
                                        <td>${employee.fullName}</td>
                                        <td class="duration"><fmt:formatNumber value="${PTO}" minFractionDigits="2"/></td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>

                    <form action="DeductPTOHandler" method="POST">
                        <input type="hidden" name="week" value="${week.name}"/>
                        <c:forEach var="employeeEntry" items="${directory.byUserName}">
                            <c:set var="employee" value="${employeeEntry.value}"/>
                            <c:set var="timesheet" value="${sarariman.timesheets.map[employee][week]}"/>
                            <c:set var="PTO" value="${timesheet.PTOHours}"/>
                            <c:if test="${PTO gt 0}">
                                <input type="hidden" name="employee" value="${employee.number}"/>
                                <input type="hidden" name="PTO" value="${PTO}"/>
                            </c:if>
                        </c:forEach>
                        <input type="submit" value="Deduct"/> <!-- FIXME: Need a way to disable this when the week has already been done. -->
                    </form>

                    <%@include file="footer.jsp" %>
                </div>
            </c:otherwise>
        </c:choose>
    </body>
</html>
