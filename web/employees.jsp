<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
        <title>Employees</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>
        <div class="container-fluid">

            <h1>Employees</h1>

            <c:choose>
                <c:when test="${param.showInactive}">
                    <a class="btn" href="${request.requestURI}?showInactive=false">Hide inactive</a>
                </c:when>
                <c:otherwise>
                    <a class="btn" href="${request.requestURI}?showInactive=true">Show inactive</a>
                </c:otherwise>
            </c:choose>

            <ul>
                <c:forEach var="employeeEntry" items="${directory.byUserName}">
                    <c:set var="employee" value="${employeeEntry.value}"/>
                    <c:if test="${employee.active || param.showInactive}">
                        <li>
                            <a href="${employee.URL}">${employee.fullName}</a>
                            <a href="${employee.URL}"><img class="img-rounded" width="25" height="25" onerror="this.style.display='none'" src="${employee.photoURL}"/></a>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
