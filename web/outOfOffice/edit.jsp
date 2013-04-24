<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
        <title>Edit Out of Office Time</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />
        <script type="text/javascript" src="../jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script type="text/javascript" src="../js/jquery-ui-timepicker-addon.js"></script>
        <link href="../style/timepicker.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript">
            $(function() {
                $( "#begin" ).datetimepicker({dateFormat: 'yy-mm-dd'});
                $( "#end" ).datetimepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT begin, end, comment
                FROM out_of_office
                WHERE id = ?
                <sql:param value="${param.id}"/>
            </sql:query>

            <!-- FIXME: Validate that end is greater than begin. -->

            <form method="POST" action="handleEdit.jsp">
                <p>
                    <label for="begin">Begin: </label>
                    <input size="17" type="text" id="begin" name="begin" value="${resultSet.rows[0].begin}"/>

                    <label for="end">End: </label>
                    <input size="17" type="text" id="end" name="end" value="${resultSet.rows[0].end}"/><br/>

                    <label for="comment">Comment: </label>
                    <input size="50" type="text" id="comment" name="comment" value="${fn:escapeXml(resultSet.rows[0].comment)}"/><br/>

                    <input type="hidden" name="id" value="${param.id}"/>
                    <input type="submit" value="Update" name="update"/>
                </p>
            </form>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
