<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sql:update dataSource="jdbc/sarariman">
    DELETE FROM company_events_rsvp
    WHERE event = ? AND employee = ?
    <sql:param value="${param.event}"/>
    <sql:param value="${param.employee}"/>
</sql:update>
<sql:update dataSource="jdbc/sarariman">
    INSERT INTO company_events_rsvp(event, employee, attending) VALUES(?, ?, ?)
    <sql:param value="${param.event}"/>
    <sql:param value="${param.employee}"/>
    <sql:param value="${param.attending}"/>
</sql:update>
<c:url var="eventLink" value="view">
    <c:param name="id" value="${param.event}"/>
</c:url>
<c:redirect url="${eventLink}"/>
