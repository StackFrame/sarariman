<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Uninvoiced Projects</title>
    </head>

    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <h1>Uninvoiced Projects</h1>

            <sql:query dataSource="jdbc/sarariman" var="result">
                SELECT DISTINCT p.id
                FROM hours as h
                JOIN tasks AS t ON h.task = t.id
                JOIN projects AS p ON t.project = p.id
                JOIN customers AS c ON c.id = p.customer
                LEFT OUTER JOIN invoices AS i ON i.employee = h.employee AND i.task = h.task AND i.date = h.date
                WHERE t.billable = 1 AND i.id IS NULL AND h.duration > 0 AND h.service_agreement IS NULL
            </sql:query>
            <!-- FIXME: Need to collect service only projects with unbilled services. -->
            <ul>
                <c:forEach var="row" items="${result.rows}" varStatus="varStatus">
                    <li>
                        <c:set var="project" value="${sarariman.projects.map[row.id]}"/>
                        <a href="uninvoiced?project=${row.id}">${fn:escapeXml(project.name)} - ${fn:escapeXml(project.client.name)}</a>
                    </li>
                </c:forEach>
            </ul>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
