<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sql:query dataSource="jdbc/sarariman" var="ticketResultSet">
    SELECT created, employee_creator, creator_latitude, creator_longitude
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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>${fn:escapeXml(name)}</title>
    </head>
    <body>
        <%@include file="../header.jsp" %>
        <h1>${fn:escapeXml(name)}</h1>

        <p>
            Employee Creator: ${directory.byNumber[ticket.employee_creator].displayName}<br/>
            <c:if test="${ticket.creator_latitude != 0 and ticket.creator_longitude != 0}">
                <c:url var="mapURL" value="https://maps.google.com/maps">
                    <c:param name="q" value="${ticket.creator_latitude},${ticket.creator_longitude}"/>
                </c:url>
                Location: <a href="${mapURL}">${ticket.creator_latitude},${ticket.creator_longitude}</a>
            </c:if>
        </p>

        <%@include file="../footer.jsp" %>
    </body>
</html>
