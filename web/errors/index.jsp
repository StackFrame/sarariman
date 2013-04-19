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
        <title>Errors</title>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <%@include file="../header.jsp" %>

        <table>            
            <caption>Errors</caption>
            <tr>
                <th>Time</th>
                <th>ID</th>
                <th>Employee</th>
            </tr>
            <c:forEach var="error" items="${sarariman.errors.all}">
                <tr>
                    <td><a href="${error.URL}">${error.timestamp}</a></td>
                    <td><a href="${error.URL}">${error.id}</a></td>
                    <td><a href="${error.URL}">${error.employee.userName}</a></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="../footer.jsp" %>
    </body>
</html>
