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
        <link href="../css/style.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>Upcoming Holidays</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">
            <h1>Upcoming Holidays</h1>

            <table class="table" id="holidays">
                <tr><th>Date</th><th>Holiday</th></tr>
                <c:forEach var="holiday" items="${sarariman.holidays.upcoming}">
                    <tr>
                        <td>${holiday.date}</td>
                        <td>${holiday.description}</td>
                    </tr>
                </c:forEach>
            </table>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
