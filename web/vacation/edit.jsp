<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>Edit Vacation Time</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />    
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
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
        <%@include file="../header.jsp" %>
        <h1>Edit Vacation Time</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT comment
            FROM vacation
            WHERE employee = ? AND begin = ? AND end = ?
            <sql:param value="${user.number}"/>
            <sql:param value="${param.begin}"/>
            <sql:param value="${param.end}"/>
        </sql:query>

        <!-- FIXME: Validate that end is greater than begin. -->

        <form method="POST" action="handleEdit.jsp">
            <p>
                <label for="begin">Begin: </label>
                <input size="10" type="text" id="begin" name="begin" value="${param.begin}"/>
                <input type="hidden" name="existingBegin" value="${param.begin}"/>

                <label for="end">End: </label>
                <input size="10" type="text" id="end" name="end" value="${param.end}"/><br/>
                <input type="hidden" name="existingEnd" value="${param.end}"/>

                <label for="comment">Comment: </label>
                <input size="50" type="text" id="comment" name="comment" value="${fn:escapeXml(resultSet.rows[0].comment)}"/><br/>

                <input type="submit" value="Update" name="update"/>
            </p>
        </form>

        <%@include file="../footer.jsp" %>
    </body>
</html>
