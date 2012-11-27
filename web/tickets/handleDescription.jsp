<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sql:update dataSource="jdbc/sarariman">
    INSERT INTO ticket_description (ticket, description, employee) VALUES(?, ?, ?)
    <sql:param value="${param.id}"/>
    <sql:param value="${param.description}"/>
    <sql:param value="${user.number}"/>
</sql:update>
<c:redirect url="view.jsp">
    <c:param name="id" value="${param.id}"/>
</c:redirect>