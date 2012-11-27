<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sql:update dataSource="jdbc/sarariman">
    INSERT INTO ticket_assignment (ticket, assignee, assignor, assignment) VALUES(?, ?, ?, ?)
    <sql:param value="${param.id}"/>
    <sql:param value="${param.assignee}"/>
    <sql:param value="${user.number}"/>
    <sql:param value="${param.assignment}"/>
</sql:update>
<c:redirect url="view.jsp">
    <c:param name="id" value="${param.id}"/>
</c:redirect>