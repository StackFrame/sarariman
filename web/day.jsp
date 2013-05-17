<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

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
        <title>View Day</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

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

            <table id="days" class="table table-bordered table-rounded table-striped">
                <thead>
                    <tr><th>Employee</th><th>Hours</th></tr>
                </thead>
                <tbody>
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
                </tbody>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
