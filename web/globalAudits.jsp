<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
        <title>Global Audits</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

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
        </div>
    </body>
</html>
