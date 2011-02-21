<%--
  Copyright (C) 2009-2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Employees</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Employees</h1>

        <c:choose>
            <c:when test="${param.showInactive}">
                <a href="${request.requestURI}?showInactive=false">Hide inactive</a>
            </c:when>
            <c:otherwise>
                <a href="${request.requestURI}?showInactive=true">Show inactive</a>
            </c:otherwise>
        </c:choose>

        <ul>
            <c:forEach var="employeeEntry" items="${directory.byUserName}">
                <c:set var="employee" value="${employeeEntry.value}"/>
                <c:if test="${employee.active || param.showInactive}">
                    <li>
                        <c:url var="link" value="employee">
                            <c:param name="id" value="${employee.number}"/>
                        </c:url>
                        <a href="${fn:escapeXml(link)}">${employee.fullName}</a>
                    </li>
                </c:if>
            </c:forEach>
        </ul>

        <%@include file="footer.jsp" %>
    </body>
</html>
