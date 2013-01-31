<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Global Audits</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Global Audits</h1>
        <ol>
            <c:forEach var="audit" items="${sarariman.globalAudits}">
                <li>
                    ${audit.displayName}
                    <ol>
                        <c:forEach var="result" items="${audit.results}">
                            <c:if test="${not result.okay}">
                                <li class="error">${result.type}: ${result.message}</li>
                            </c:if>
                        </c:forEach>
                    </ol>
                </li>
            </c:forEach>
        </ol>

        <%@include file="footer.jsp" %>
    </body>
</html>
