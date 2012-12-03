<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sql:transaction dataSource="jdbc/sarariman">
    <sql:update>
        INSERT INTO ticket (employee_creator, creator_IP, has_creator_location, creator_latitude, creator_longitude, creator_user_agent) VALUES(?, ?, ?, ?, ?, ?)
        <sql:param value="${user.number}"/>
        <sql:param value="${pageContext.request.remoteAddr}"/>
        <sql:param value="${param.has_creator_location}"/>
        <sql:param value="${param.latitude}"/>
        <sql:param value="${param.longitude}"/>
        <sql:param value="${header['User-Agent']}"/>
    </sql:update>
    <sql:query var="resultSet">
        SELECT LAST_INSERT_ID() AS id FROM ticket
    </sql:query>
    <c:set var="ticket_id" value="${resultSet.rows[0].id}"/>
    <sql:update>
        INSERT INTO ticket_name (ticket, name, employee) VALUES(?, ?, ?)
        <sql:param value="${ticket_id}"/>
        <sql:param value="${param.name}"/>
        <sql:param value="${user.number}"/>
    </sql:update>
    <sql:update>
        INSERT INTO ticket_assignment (ticket, assignee, assignor, assignment) VALUES(?, ?, ?, 1)
        <sql:param value="${ticket_id}"/>
        <sql:param value="${user.number}"/>
        <sql:param value="${user.number}"/>
    </sql:update>
    <sql:update>
        INSERT INTO ticket_status (ticket, status, employee) VALUES(?, 'open', ?)
        <sql:param value="${ticket_id}"/>
        <sql:param value="${user.number}"/>
    </sql:update>
</sql:transaction>
<c:redirect url="${ticket_id}"/>
