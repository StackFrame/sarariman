<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:parseNumber var="task" value="${param.task}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Hours for task ${task}</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <h1>Hours for task ${task}</h1>
            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT * FROM hours WHERE task = ? ORDER BY date DESC
                <sql:param value="${task}"/>
            </sql:query>

            <table id="hours" class="table table-bordered table-striped table-rounded">
                <thead>
                    <tr><th>Date</th><th>Employee</th><th>Duration</th><th>Description</th></tr>
                </thead>
                <tbody>
                    <c:set var="total" value="0.0"/>
                    <c:forEach var="row" items="${result.rows}">
                        <tr>
                            <td class="date">${row.date}</td>
                            <td>${directory.byNumber[row.employee].fullName}</td>
                            <td class="duration">${row.duration}</td>
                            <td>${fn:escapeXml(row.description)}</td><%-- FIXME: Need to do something smarter here. --%>
                        </tr>
                        <c:set var="total" value="${total+row.duration}"/>
                    </c:forEach>
                <tbody>
                <tfoot>
                    <tr><td colspan="2">Total</td><td class="duration">${total}</td><td></td></tr>
                </tfoot>
            </table>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
