<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Labor Category Assignments</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Labor Category Assignments</h1>

        <table class="altrows" id="rates">
            <tr><th>Employee</th><th>Labor Category</th><th>Project</th><th>Customer</th><th>Start</th><th>End</th></tr>
            <c:forEach var="entry" items="${sarariman.projectBillRates}">
                <tr>
                    <td>${entry.employee.fullName}</td>
                    <c:set var="laborCategory" value="${sarariman.laborCategories[entry.laborCategory]}"/>
                    <td>${laborCategory.name}</td>
                    <c:set var="project" value="${sarariman.projects[laborCategory.project]}"/>
                    <td>${fn:escapeXml(project.name)}</td>
                    <c:set var="customer" value="${sarariman.customers[sarariman.projects[laborCategory.project].customer]}"/>
                    <td>${fn:escapeXml(customer.name)}</td>
                    <td>${entry.periodOfPerformanceStart}</td>
                    <td>${entry.periodOfPerformanceEnd}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
