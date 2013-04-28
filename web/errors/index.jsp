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
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>Errors</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">

            <table class="table table-striped">
                <caption>Errors</caption>
                <thead>
                    <tr>
                        <th>Time</th>
                        <th>ID</th>
                        <th>Employee</th>
                        <th>Remote Address</th>
                        <th>Path</th>
                        <th>Query</th>
                        <th>Method</th>
                        <th>User Agent</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="error" items="${sarariman.errors.all}">
                        <tr>
                            <td><a href="${error.URL}">${error.timestamp}</a></td>
                            <td><a href="${error.URL}">${error.id}</a></td>
                            <td><a href="${error.URL}">${error.employee.userName}</a></td>
                            <td><a href="${error.URL}">${error.remoteAddress}</a></td>
                            <td><a href="${error.URL}">${error.path}</a></td>
                            <td><a href="${error.URL}">${error.query}</a></td>
                            <td><a href="${error.URL}">${error.method}</a></td>
                            <td><a href="${error.URL}">${error.userAgent}</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
