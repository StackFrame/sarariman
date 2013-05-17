<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

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
        <title>PTO Balance</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <table id="timesheets" class="table table-bordered table-striped table-rounded">
                <thead>
                    <tr><th>Employee</th><th>Balance</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="employeeEntry" items="${directory.byUserName}">
                        <c:set var="employee" value="${employeeEntry.value}"/>
                        <sql:query dataSource="jdbc/sarariman" var="resultSet">
                            SELECT sum(amount) AS balance FROM paid_time_off WHERE employee=?
                            <sql:param value="${employee.number}"/>
                        </sql:query>
                        <c:set var="balance" value="${resultSet.rows[0].balance}"/>
                        <tr>
                            <td>${employee.fullName}</td>
                            <td class="duration"><fmt:formatNumber value="${balance}" minFractionDigits="2"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
