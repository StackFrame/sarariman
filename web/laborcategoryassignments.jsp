<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Project Bill Rates</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('rates')">
        <p><a href="./">Home</a> <a href="tools">Tools</a></p>
        <h1>Project Bill Rates</h1>

        <table class="altrows" id="rates">
            <tr><th>Employee</th><th>Labor Category</th><th>Start</th><th>End</th></tr>
            <c:forEach var="entry" items="${sarariman.projectBillRates}">
                <tr>
                    <td>${entry.employee.fullName}</td>
                    <td>${entry.laborCategory}</td>
                    <td>${entry.periodOfPerformanceStart}</td>
                    <td>${entry.periodOfPerformanceEnd}</td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
