<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <style type="text/css">
            body {
                padding-top: 60px;
            }

            /** This keeps the padding-top defined above from interfering with the responsive bootstrap CSS. */
            @media (max-width: 979px) {
                body {
                    padding-top: 0;
                }
            }
        </style>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>Upcoming Holidays</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">
            <h1>Upcoming Holidays</h1>

            <table id="holidays">
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
