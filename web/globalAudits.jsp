<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Global Audits</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Global Audits</h1>
        <ol>
            <c:forEach var="audit" items="${sarariman.globalAudits}">
                <c:set var="results" value="${audit.results}"/>
                <c:if test="${not empty results}">
                    <li>
                        ${audit.displayName}
                        <ol>
                            <c:forEach var="result" items="${results}">
                                <li class="error"><a href="${result.URL}">${result.type}: ${result.message}</a></li>
                            </c:forEach>
                        </ol>
                    </li>
                </c:if>
            </c:forEach>
        </ol>

        <%@include file="footer.jsp" %>
    </body>
</html>
