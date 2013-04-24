<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- FIXME: Authorize. -->

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
        <title>Edit Vacation Time</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />
        <script type="text/javascript" src="../jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script>
            $(function() {
                $( "#begin" ).datepicker({dateFormat: 'yy-mm-dd'});
                $( "#end" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>
            <h1>Edit Vacation Time</h1>

            <fmt:parseNumber var="id" value="${param.id}"/>
            <c:set var="entry" value="${sarariman.vacations.map[id]}" />

            <!-- FIXME: Validate that end is greater than begin. -->

            <form method="POST" action="handleEdit.jsp">
                <p>
                    <label for="begin">Begin: </label>
                    <fmt:formatDate var="beginFormatted" type="date" pattern="yyyy-MM-dd" value="${entry.begin}"/>
                    <input size="10" type="text" id="begin" name="begin" value="${beginFormatted}"/>

                    <label for="end">End: </label>
                    <fmt:formatDate var="endFormatted" type="date" pattern="yyyy-MM-dd" value="${entry.end}"/>
                    <input size="10" type="text" id="end" name="end" value="${endFormatted}"/><br/>

                    <label for="comment">Comment: </label>
                    <input size="50" type="text" id="comment" name="comment" value="${fn:escapeXml(entry.comment)}"/><br/>

                    <input type="hidden" name="id" value="${param.id}"/>
                    <input type="submit" value="Update" name="update"/>
                </p>
            </form>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
