<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Customers</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <h1>Customers</h1>

            <h2>Create a new customer</h2>
            <form method="POST" action="customerController">
                <label for="name">Name: </label>
                <input type="text" size="40" id="name" name="name" value=""/><br/>
                <input type="hidden" name="action" value="create"/>
                <input type="submit" name="create" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                </form>
                <br/>

                <table id="customers">
                    <tr><th>ID</th><th>Name</th>
                    <c:if test="${user.administrator}"><th>Action</th></c:if>
                    </tr>
                <c:forEach var="client" items="${sarariman.clients.all}">
                    <tr>
                        <td><a href="customer?id=${client.id}">${client.id}</a></td>
                        <td><a href="customer?id=${client.id}">${fn:escapeXml(client.name)}</a></td>
                        <c:if test="${user.administrator}">
                            <td>
                                <form method="POST" action="customerController">
                                    <input type="hidden" name="action" value="delete"/>
                                    <input type="hidden" name="id" value="${client.id}"/>
                                    <input type="submit" name="delete" value="Delete"/>
                                </form>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </table>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
