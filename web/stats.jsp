<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
        <title>Stats</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>Stats</h1>

            <!--
            how long in operation
            total with clearances?
            total with degrees?
            -->

            <c:set var="totalEmployees" value="0"/>
            <c:set var="totalFullTime" value="0"/>
            <c:forEach var="e" items="${directory.byUserName}">
                <c:set var="employee" value="${e.value}"/>
                <c:if test="${employee.active}">
                    <c:set var="totalEmployees" value="${totalEmployees + 1}"/>
                    <c:if test="${employee.fulltime}">
                        <c:set var="totalFullTime" value="${totalFullTime + 1}"/>
                    </c:if>
                </c:if>
            </c:forEach>

            <table class="table table-rounded table-striped table-bordered">
                <caption>Employees</caption>
                <thead>
                    <tr><th>Type</th><th>Total</th></tr>
                </thead>
                <tbody>
                    <tr><td>Full-time</td><td class="quantity">${totalFullTime}</td></tr>
                    <tr><td>Part-time</td><td class="quantity">${totalEmployees - totalFullTime}</td></tr>
                    <tr><td>Total</td><td class="quantity"><a href="employees">${totalEmployees}</a></td></tr>
                </tbody>
            </table>

            <p>

                <sql:query dataSource="jdbc/sarariman" var="resultSet">
                    SELECT COUNT(id) AS count FROM customers WHERE active = TRUE AND official = TRUE
                </sql:query>
                <c:set var="activeClients" value="${resultSet.rows[0].count}"/>
                Active clients: <a href="customers"><span class="quantity">${activeClients}</span></a><br/>

                <sql:query dataSource="jdbc/sarariman" var="resultSet">
                    SELECT COUNT(id) AS count FROM projects WHERE active = TRUE
                </sql:query>
                <c:set var="activeProjects" value="${resultSet.rows[0].count}"/>
                Active projects: <a href="projects"><span class="quantity">${activeProjects}</span></a>

            </p>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
