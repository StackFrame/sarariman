<%--
  Copyright (C) 2010-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
        <title>Contacts</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <h1>Contacts</h1>

            <c:url var="createLink" value="contact">
                <c:param name="action" value="create"/>
            </c:url>
            <a href="${createLink}">Create a new contact</a>

            <sql:query dataSource="jdbc/sarariman" var="contacts">
                SELECT * from contacts
            </sql:query>

            <table id="contacts" class="table table-bordered table-rounded table-striped">
                <thead>
                    <tr><th>Name</th><th>Title</th><th>Phone</th><th>Email</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="contact" items="${contacts.rows}">
                        <tr>
                            <c:url var="link" value="contact">
                                <c:param name="id" value="${contact.id}"/>
                            </c:url>
                            <td><a href="${link}" title="edit contact information for ${contact.name}">${contact.name}</a></td>
                            <td><a href="${link}" title="edit contact information for ${contact.name}">${contact.title}</a></td>
                            <td><a href="${link}" title="edit contact information for ${contact.name}">${contact.phone}</a></td>
                            <td><a href="mailto:${contact.email}" title="send email to ${contact.name}">${contact.email}</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
