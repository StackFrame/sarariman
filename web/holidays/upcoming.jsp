<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>Upcoming Holidays</title>
        <script type="text/javascript" src="../utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="../header.jsp" %>

        <h1>Upcoming Holidays</h1>

        <table class="altrows" id="holidays">
            <tr><th>Date</th><th>Holiday</th></tr>
            <c:forEach var="holiday" items="${sarariman.holidays.upcoming}">
                <tr>
                    <td>${holiday.date}</td>
                    <td>${holiday.description}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="../footer.jsp" %>
    </body>
</html>
