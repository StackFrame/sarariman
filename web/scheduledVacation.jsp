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
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Scheduled Vacation</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>
        <div class="container-fluid">

            <h1>Scheduled Vacation</h1>

            <!-- FIXME: filter by management relationship (all indirect reports) -->

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT employee, begin, end, comment
                FROM vacation
                WHERE begin >= DATE(NOW()) OR end >= DATE(NOW())
                ORDER BY begin
            </sql:query>
            <c:if test="${resultSet.rowCount != 0}">
                <ul>
                    <c:forEach var="row" items="${resultSet.rows}">
                        <li>
                            ${directory.byNumber[row.employee].fullName}:
                            <!-- FIXME: This could be much nicer. e.g., "May 21, 2012 - May 25, 2012" could be "May 21 - 25, 2012", "May 21, 2012 - June 2, 2012" could be "May 21 - June 2, 2012". -->
                            <c:choose>
                                <c:when test="${row.begin eq row.end}">
                                    <fmt:formatDate value="${row.begin}" type="date" dateStyle="long" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${row.begin}" type="date" dateStyle="long" /> -
                                    <fmt:formatDate value="${row.end}" type="date" dateStyle="long" />
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${!empty row.comment}">
                                - ${fn:escapeXml(row.comment)}
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
