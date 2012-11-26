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
        <title>Edit Event</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />    
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
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
        <%@include file="../header.jsp" %>
        <h1>Edit Event</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT begin, end, name, location, location_url, location_map_url, description, creator
            FROM company_events
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

                <label for="name">Name: </label>
                <input size="50" type="text" id="name" name="name" value="${fn:escapeXml(resultSet.rows[0].name)}"/><br/>

                <label for="location">Location: </label>
                <input size="50" type="text" id="location" name="location" value="${fn:escapeXml(resultSet.rows[0].location)}"/><br/>

                <label for="location_url">Location URL: </label>
                <input size="50" type="text" id="location_url" name="location_url" value="${fn:escapeXml(resultSet.rows[0].location_url)}"/><br/>

                <label for="location_map_url">Location Map URL: </label>
                <input size="50" type="text" id="location_map_url" name="location_map_url" value="${fn:escapeXml(resultSet.rows[0].location_map_url)}"/><br/>

                <label for="description">Description: </label>
                <textarea rows="3" cols="70" id="description" name="description">${fn:escapeXml(resultSet.rows[0].description)}</textarea><br/>

                <input type="hidden" name="id" value="${param.id}"/>
                <input type="submit" value="Update" name="update" <c:if test="${user.number ne resultSet.rows[0].creator}">disabled="true"</c:if>/>
            </p>
        </form>

        <%@include file="../footer.jsp" %>
    </body>
</html>
