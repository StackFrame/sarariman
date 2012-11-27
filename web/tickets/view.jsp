<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@page import="com.stackframe.sarariman.Sarariman"%>
<%@page import="com.stackframe.sarariman.tickets.Ticket"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sql:query dataSource="jdbc/sarariman" var="ticketResultSet">
    SELECT created, employee_creator, has_creator_location, creator_latitude, creator_longitude, creator_user_agent, creator_IP
    FROM ticket
    WHERE id = ?
    <sql:param value="${param.id}"/>
</sql:query>
<c:set var="ticket" value="${ticketResultSet.rows[0]}"/>
<sql:query dataSource="jdbc/sarariman" var="ticketNameResultSet">
    SELECT name
    FROM ticket_name
    WHERE ticket = ?
    <sql:param value="${param.id}"/>
</sql:query>
<c:set var="name" value="${ticketNameResultSet.rows[0].name}"/>

<%
    Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    int ticket_id = Integer.parseInt(request.getParameter("id"));
    Ticket ticket = new Ticket(ticket_id, sarariman);
    pageContext.setAttribute("history", ticket.getHistory());
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>Ticket ${param.id}: ${fn:escapeXml(name)}</title>
    </head>
    <body>
        <%@include file="../header.jsp" %>
        <h1>Ticket ${param.id}: ${fn:escapeXml(name)}</h1>

        <form method="POST" action="handleNameChange.jsp">
            <label for="name">Name: </label>
            <input type="hidden" name="id" value="${param.id}"/>
            <input size="50" type="text" id="name" name="name" value="${name}"/>
            <input type="submit" value="Change Name" name="update"/>
        </form>

        <h2>Assignees</h2>
        <ol>
            <!-- FIXME: There must be a smarter way to do this in SQL instead of doing two queries. -->
            <sql:query dataSource="jdbc/sarariman" var="assigneeResultSet">
                SELECT DISTINCT assignee FROM ticket_assignment WHERE ticket = ?
                <sql:param value="${param.id}"/>
            </sql:query>
            <c:forEach var="assigneeRow" items="${assigneeResultSet.rows}">
                <sql:query dataSource="jdbc/sarariman" var="sumResultSet">
                    SELECT SUM(assignment) AS sum FROM ticket_assignment WHERE ticket = ? AND assignee = ?
                    <sql:param value="${param.id}"/>
                    <sql:param value="${assigneeRow.assignee}"/>
                </sql:query>
                <c:if test="${sumResultSet.rows[0].sum gt 0}">
                    <li>${directory.byNumber[assigneeRow.assignee].displayName}</li>
                </c:if>
            </c:forEach>
        </ol>
        <p>
            Employee Creator: ${directory.byNumber[ticket.employee_creator].displayName}<br/>
            <c:if test="${ticket.has_creator_location}">
                <c:url var="mapURL" value="https://maps.google.com/maps">
                    <c:param name="q" value="${ticket.creator_latitude},${ticket.creator_longitude}"/>
                </c:url>
                Location: <a href="${mapURL}">${ticket.creator_latitude},${ticket.creator_longitude}</a><br/>
            </c:if>
            Creator IP: ${fn:escapeXml(ticket.creator_IP)}<br/>
            Creator User Agent: ${fn:escapeXml(ticket.creator_user_agent)}
        </p>

        <h2>History</h2>
        <ol>
            <c:forEach var="item" items="${history}">
                <li>${item.timestamp}: ${item.text}</li>
            </c:forEach>
        </ol>
        <%@include file="../footer.jsp" %>
    </body>
</html>
