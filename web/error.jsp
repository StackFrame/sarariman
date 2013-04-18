<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Error</h1>

        <p>Yikes! There was an error.</p>

        <p>The error has been logged and whoever is responsible will be found and flogged.</p>

        <c:if test="${not empty stacktrace}">
            <p>There was an exception:</p>
            <pre>
${stacktrace}
            </pre>
        </c:if>

        <%@include file="footer.jsp" %>
    </body>
</html>
