<%--
  Copyright (C) 2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Contacts</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Contacts</h1>

        <c:url var="createLink" value="contact">
            <c:param name="action" value="create"/>
        </c:url>
        <a href="${createLink}">Create a new contact</a>

        <sql:query dataSource="jdbc/sarariman" var="contacts">
            SELECT * from contacts
        </sql:query>

        <table id="contacts" class="altrows">
            <tr><th>Name</th><th>Title</th></tr>
            <c:forEach var="contact" items="${contacts.rows}">
                <tr>
                    <c:url var="link" value="contact">
                        <c:param name="id" value="${contact.id}"/>
                    </c:url>
                    <td><a href="${link}">${contact.name}</a></td>
                    <td><a href="${link}">${contact.title}</a></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
