<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>View Day</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('days')">
        <p><a href="./">Home</a></p>
        <c:choose>
            <c:when test="${!empty param.day}">
                <c:set var="day" value="${param.day}"/>
            </c:when>
            <c:otherwise>
                <fmt:formatDate var="day" value="${du:now()}" type="date" pattern="yyyy-MM-dd"/>
            </c:otherwise>
        </c:choose>
        <h1>Activity for ${day}</h1>
        <h2>View another day</h2>
        <form method="get">
            <label for="day">Date: </label>
            <input type="text" value="${day}" id="day" name="day"/>
            <input type="submit" value="View"/>
        </form>
        <table class="altrows" id="days">
            <tr><th>Employee</th><th>Hours</th></tr>
            <c:forEach var="employeeEntry" items="${directory.byUserName}">
                <sql:query dataSource="jdbc/sarariman" var="data">
                    SELECT * FROM hours WHERE date=? AND employee=? AND duration>0
                    <sql:param value="${day}"/>
                    <sql:param value="${employeeEntry.value.number}"/>
                </sql:query>
                <c:set var="totalHours" value="0"/>
                <c:forEach var="row" items="${data.rows}">
                    <c:set var="totalHours" value="${totalHours + row.duration}"/>
                </c:forEach>
                <tr><td>${employeeEntry.value.fullName}</td><td>${totalHours}</td></tr>
            </c:forEach>
        </table>
    </body>
</html>
