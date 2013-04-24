<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>Error</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <h1>Error</h1>

            <fmt:parseNumber var="errorNumber" value="${param.id}"/>
            <c:set var="error" value="${sarariman.errors.map[errorNumber]}"/>

            <p>
                ID: ${error.id}<br/>
                Timestamp: ${error.timestamp}<br/>
                Employee: ${error.employee.userName}<br/>
                Remote Address: ${error.remoteAddress}<br/>
                Path: ${error.path}<br/>
                Query: ${error.query}<br/>
                Method: ${error.method}<br/>
                User Agent: ${error.userAgent}
            </p>

            <pre>
                ${error.stackTrace}
            </pre>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
