<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

    <fmt:parseNumber var="customer_id" value="${param.id}"/>
    <c:set var="customer" value="${sarariman.clients.map[customer_id]}"/>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Customer ${customer.id}</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <h1>Customer ${customer.id}</h1>
            <form method="POST" action="customerController">
                <label for="name">Name: </label>
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="id" value="${customer.id}"/>
                <input type="text" id="name" name="name" size="40" value="${fn:escapeXml(customer.name)}"/><br/>
                <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
            </form>

            <h2>Projects</h2>
            <table id="projects">
                <tr><th>ID</th><th>Name</th></tr>
                <c:forEach var="project" items="${sarariman.projects.all}">
                    <c:if test="${project.client.id == customer.id}">
                        <tr>
                            <td><a href="${project.URL}">${project.id}</a></td>
                            <td><a href="${project.URL}">${fn:escapeXml(project.name)}</a></td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>

            <c:url var="invoicesLink" value="invoicesByCustomer.jsp">
                <c:param name="customer" value="${param.id}"/>
            </c:url>
            <h2><a href="${invoicesLink}">Invoices</a></h2>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
