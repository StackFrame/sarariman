<%--
  Copyright (C) 2009-2013 StackFrame, LLC
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
                    UPDATE labor_categories
                    SET name=?, project=?, rate=?, pop_start=?, pop_end=?
                    WHERE id=?
                    <sql:param value="${param.name}"/>
                    <sql:param value="${param.project}"/>
                    <sql:param value="${param.rate}"/>
                    <sql:param value="${param.pop_start}"/>
                    <sql:param value="${param.pop_end}"/>
                    <sql:param value="${param.id}"/>
                </sql:update>
            </c:when>
            <c:when test="${!empty param.create}">
                <sql:transaction dataSource="jdbc/sarariman">
                    <sql:update>
                        INSERT INTO labor_categories
                        (name, project, rate, pop_start, pop_end)
                        VALUES(?, ?, ?, ?, ?);
                        <sql:param value="${param.name}"/>
                        <sql:param value="${param.project}"/>
                        <sql:param value="${param.rate}"/>
                        <sql:param value="${param.pop_start}"/>
                        <sql:param value="${param.pop_end}"/>
                    </sql:update>
                    <sql:query var="insertResult">
                        SELECT LAST_INSERT_ID()
                    </sql:query>
                </sql:transaction>
                <c:set var="id" value="${insertResult.rowsByIndex[0][0]}"/>
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
        <title>Labor Category ${id}</title>

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
                FROM labor_categories
                WHERE id=?
                <sql:param value="${id}"/>
            </sql:query>
            <c:set var="laborcategory" value="${result.rows[0]}"/>

            <h1>Labor Category ${id}</h1>
            <form method="POST">
                <label for="name">Name: </label>
                <input type="text" id="name" name="name" value="${fn:escapeXml(laborcategory.name)}"/><br/>

                <label for="project">Project: </label>
                <select id="project" name="project">
                    <c:forEach var="project" items="${sarariman.projects.all}">
                        <c:set var="customer" value="${project.client}"/>
                        <option value="${project.id}" <c:if test="${laborcategory.project == project.id}">selected="selected"</c:if>>${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</option>
                    </c:forEach>
                </select><br/>

                <label for="rate">Rate: </label>
                <input type="text" id="rate" name="rate" value="${laborcategory.rate}"/><br/>

                <label for="pop_start">Period of Performance Start: </label>
                <fmt:formatDate var="pop_start" value="${laborcategory.pop_start}" type="date" pattern="yyyy-MM-dd"/>
                <input size="10" type="text" id="pop_start" name="pop_start" value="${pop_start}"/>

                <label for="pop_end">End: </label>
                <fmt:formatDate var="pop_end" value="${laborcategory.pop_end}" type="date" pattern="yyyy-MM-dd"/>
                <input size="10" type="text" id="pop_end" name="pop_end" value="${pop_end}"/><br/>

                <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                </form>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
