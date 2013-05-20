<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

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
        <title>Invoices</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <!-- FIXME: use invoice_info directly once we have fixed all of the entries. -->

            <sql:query dataSource="jdbc/sarariman" var="invoices">
                SELECT DISTINCT(id) FROM invoice_info ORDER BY id DESC
            </sql:query>

            <table id="invoices" class="table table-bordered table-striped table-rounded">
                <caption>Invoices</caption>
                <thead>
                    <tr><th>Invoice</th><th>Sent</th><th>Project</th><th>Customer</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="invoice" items="${invoices.rows}">
                        <tr>
                            <c:url var="link" value="invoice">
                                <c:param name="invoice" value="${invoice.id}"/>
                            </c:url>
                            <td><a href="${link}">${invoice.id}</a></td>
                            <sql:query dataSource="jdbc/sarariman" var="invoice_info_result">
                                SELECT project, customer, sent
                                FROM invoice_info AS i
                                WHERE i.id = ?
                                <sql:param value="${invoice.id}"/>
                            </sql:query>
                            <c:set var="invoice_info" value="${invoice_info_result.rows[0]}"/>
                            <c:set var="project" value="${sarariman.projects.map[invoice_info.project]}"/>
                            <c:set var="customer" value="${project.client}"/>
                            <fmt:formatDate var="sent" value="${invoice_info.sent}"/>

                            <c:choose>
                                <c:when test="${empty sent}">
                                    <td class="error">no date</td>
                                </c:when>
                                <c:otherwise>
                                    <td>${sent}</td>
                                </c:otherwise>
                            </c:choose>
                            <td>${fn:escapeXml(project.name)}</td>
                            <td>${fn:escapeXml(customer.name)}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
