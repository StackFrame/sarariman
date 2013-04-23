<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>

    <c:set var="id" value="${param.id}"/>

    <c:if test="${user.administrator}">
        <c:choose>
            <c:when test="${!empty param.update}">
                <sql:update dataSource="jdbc/sarariman">
                    UPDATE labor_category_assignments
                    SET employee=?, pop_start=?, pop_end=?
                    WHERE id=?
                    <sql:param value="${param.employee}"/>
                    <sql:param value="${param.pop_start}"/>
                    <sql:param value="${param.pop_end}"/>
                    <sql:param value="${param.id}"/>
                </sql:update>
            </c:when>
        </c:choose>
    </c:if>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Labor Category Assignment ${id}</title>

        <!-- jQuery -->
        <link type="text/css" href="jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />	
        <script type="text/javascript" src="jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script>
            $(function() {
                $( "#pop_start" ).datepicker({dateFormat: 'yy-mm-dd'});
                $( "#pop_end" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT *
                FROM labor_category_assignments
                WHERE id=?
                <sql:param value="${id}"/>
            </sql:query>
            <c:set var="laborcategoryassignment" value="${result.rows[0]}"/>

            <h1>Labor Category Assignment ${id}</h1>
            <form method="POST">
                <label for="employee">Employee: </label>
                <input size="10" type="text" id="employee" name="employee" value="${laborcategoryassignment.employee}"/><br/>

                <label for="pop_start">Period of Performance Start: </label>
                <fmt:formatDate var="pop_start" value="${laborcategoryassignment.pop_start}" type="date" pattern="yyyy-MM-dd"/>
                <input size="10" type="text" id="pop_start" name="pop_start" value="${pop_start}"/>

                <label for="pop_end">End: </label>
                <fmt:formatDate var="pop_end" value="${laborcategoryassignment.pop_end}" type="date" pattern="yyyy-MM-dd"/>
                <input size="10" type="text" id="pop_end" name="pop_end" value="${pop_end}"/><br/>

                <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                </form>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
