<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>Tickets</title>
        <script type="text/javascript" src="../utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="../header.jsp" %>
        <h1>Tickets</h1>

        <sql:query dataSource="jdbc/sarariman" var="ticketResultSet">
            SELECT id, created, employee_creator, has_creator_location, creator_latitude, creator_longitude, creator_user_agent, creator_IP
            FROM ticket
        </sql:query>

        <a href="create.jsp">Create a Ticket</a>
        <br/>
        <br/>

        <table class="altrows">
            <tr>
                <th>#</th><th>Name</th><th>Status</th>
            </tr>
            <c:forEach var="ticket" items="${ticketResultSet.rows}">
                <sql:query dataSource="jdbc/sarariman" var="ticketNameResultSet">
                    SELECT name
                    FROM ticket_name
                    WHERE ticket = ?
                    ORDER BY updated DESC LIMIT 1
                    <sql:param value="${ticket.id}"/>
                </sql:query>
                <c:set var="name" value="${ticketNameResultSet.rows[0].name}"/>

                <c:url var="ticketViewURL" value="view">
                    <c:param name="id" value="${ticket.id}"/>
                </c:url>

                <sql:query dataSource="jdbc/sarariman" var="statusResultSet">
                    SELECT status FROM ticket_status WHERE ticket = ?
                    ORDER BY updated DESC
                    LIMIT 1
                    <sql:param value="${ticket.id}"/>
                </sql:query>
                <c:set var="status" value="${statusResultSet.rows[0].status}"/>

                <c:set var="skip" value="false"/>

                <c:if test="${not empty param.status and param.status ne status}">
                    <c:set var="skip" value="true"/>
                </c:if>

                <c:if test="${not empty param.notStatus and param.notStatus eq status}">
                    <c:set var="skip" value="true"/>
                </c:if>

                <c:if test="${not skip}">
                    <tr>
                        <td><a href="${ticketViewURL}">${ticket.id}</a></td>
                        <td><a href="${ticketViewURL}">${fn:escapeXml(name)}</a></td>
                        <td><a href="${ticketViewURL}">${status}</a></td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>

        <%@include file="../footer.jsp" %>
    </body>
</html>
