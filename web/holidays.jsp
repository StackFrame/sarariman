<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title><c:if test="${not empty param.year}">${param.year} </c:if>Holidays</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1><c:if test="${not empty param.year}">${param.year} </c:if>Holidays</h1>

        <table class="altrows" id="holidays">
            <tr><th>Date</th><th>Holiday</th></tr>
            <c:forEach var="holiday" items="${sarariman.holidays.all}">
                <c:if test="${empty param.year or fn:startsWith(holiday.date, param.year)}">
                <tr>
                    <td>${holiday.date}</td>
                    <td>${holiday.description}</td>
                </tr>
                </c:if>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
