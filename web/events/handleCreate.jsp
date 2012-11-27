<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- FIXME: Validate begin and end. -->

<sql:transaction dataSource="jdbc/sarariman">
    <sql:update>
        INSERT INTO company_events (begin, end, name, location, location_url, location_map_url, description, creator) VALUES(?, ?, ?, ?, ?, ?, ?, ?)
        <sql:param value="${param.begin}"/>
        <sql:param value="${param.end}"/>
        <sql:param value="${param.name}"/>
        <sql:param value="${param.location}"/>
        <sql:param value="${param.location_url}"/>
        <sql:param value="${param.location_map_url}"/>
        <sql:param value="${param.description}"/>
        <sql:param value="${user.number}"/>
    </sql:update>
    <sql:query var="resultSet">
        SELECT LAST_INSERT_ID() AS id FROM company_events
    </sql:query>
</sql:transaction>
<c:redirect url="edit.jsp">
    <c:param name="id" value="${resultSet.rows[0].id}"/>
</c:redirect>