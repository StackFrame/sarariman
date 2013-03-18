<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>Edit Line Item</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />    
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="../jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script type="text/javascript">            
            $(function() {
                $( "#pop_start" ).datepicker({dateFormat: 'yy-mm-dd'});
                $( "#pop_end" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body>
        <%@include file="../header.jsp" %>
        <h1>Edit Line Item</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT funded, description, pop_start, pop_end
            FROM line_items
            WHERE id = ? and project = ?
            <sql:param value="${param.id}"/>
            <sql:param value="${param.project}"/>
        </sql:query>
        <c:set var="lineItem" value="${resultSet.rows[0]}"/>

        <!-- FIXME: Validate that PoP end is greater than begin. -->

        <form method="POST" action="handleEdit.jsp">
            <p>
                <label for="pop_start">Period of Performance Start: </label>
                <input size="10" type="text" id="pop_start" name="pop_start" value="${lineItem.pop_start}"/>

                <label for="pop_end">End: </label>
                <input size="10" type="text" id="pop_end" name="pop_end" value="${lineItem.pop_end}"/><br/>

                <label for="funded">funded: </label>
                <input size="10" type="text" id="funded" name="funded" value="${lineItem.funded}"/><br/>

                <label for="description">Description </label>
                <input size="50" type="text" id="description" name="description" value="${fn:escapeXml(lineItem.description)}"/><br/>

                <input type="hidden" name="id" value="${param.id}"/>
                <input type="hidden" name="project" value="${param.project}"/>
                <input type="submit" value="Update" name="update"/>
            </p>
        </form>

        <%@include file="../footer.jsp" %>
    </body>
</html>
