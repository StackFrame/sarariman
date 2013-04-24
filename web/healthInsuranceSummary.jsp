<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${!user.benefitsAdministrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Health Insurance Summary</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <h1>Health Insurance Summary</h1>
            <h2>Current Rates and Participation</h2>
            <!-- FIXME: Add coverage type? -->

            <table>
                <tr>
                    <th>Employee</th>
                    <th>Premium</th>
                </tr>
                <c:set var="totalPremium" value="0.0"/>
                <c:set var="employeeCount" value="0"/>
                <c:forEach var="entry" items="${directory.byUserName}">
                    <c:set var="employee" value="${entry.value}"/>
                    <c:if test="${employee.active and employee.fulltime}">
                        <tr>
                            <td>${entry.value.fullName}</td>
                            <c:set var="premium" value="${entry.value.monthlyHealthInsurancePremium}"/>
                            <td class="currency"><fmt:formatNumber type="currency" value="${premium}"/></td>
                            <c:set var="totalPremium" value="${totalPremium + premium}"/>
                            <c:set var="employeeCount" value="${employeeCount + 1}"/>
                        </tr>
                    </c:if>
                </c:forEach>
                <tr>
                    <td><strong>Total</strong></td><td class="currency"><fmt:formatNumber type="currency" value="${totalPremium}"/></td>
                </tr>
            </table>

            <p>Average per salaried employee: <fmt:formatNumber type="currency" value="${totalPremium / employeeCount}"/></p>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
