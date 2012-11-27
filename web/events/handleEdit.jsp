<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- FIXME: Validate begin and end. -->

<sql:query dataSource="jdbc/sarariman" var="resultSet">
    SELECT creator
    FROM company_events
    WHERE id = ?
    <sql:param value="${param.id}"/>
</sql:query>
<c:if test="${user.number eq resultSet.rows[0].creator}">
    <sql:update dataSource="jdbc/sarariman">
        UPDATE company_events
        SET begin = ?, end = ?, name = ?, location = ?, location_url = ?, location_map_url = ?, description = ?
        WHERE id = ?
        <sql:param value="${param.begin}"/>
        <sql:param value="${param.end}"/>
        <sql:param value="${param.name}"/>
        <sql:param value="${param.location}"/>
        <sql:param value="${param.location_url}"/>
        <sql:param value="${param.location_map_url}"/>
        <sql:param value="${param.description}"/>
        <sql:param value="${param.id}"/>
    </sql:update>
</c:if>
<c:redirect url="view">
    <c:param name="id" value="${param.id}"/>
</c:redirect>
