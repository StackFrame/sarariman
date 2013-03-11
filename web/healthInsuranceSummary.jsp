<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Health Insurance Summary</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Health Insurance Summary</h1>
        <h2>Current Rates and Participation</h2>
        <!-- FIXME: Add coverage type? -->

        <table class="altrows">
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
    </body>
</html>
