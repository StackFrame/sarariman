<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Scheduled Vacation</title>
    </head>
    <body>
        <%@include file="header.jsp" %>
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
    </body>
</html>
