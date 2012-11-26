<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sql:query dataSource="jdbc/sarariman" var="resultSet">
    SELECT creator
    FROM company_events
    WHERE id = ?
    <sql:param value="${param.id}"/>
</sql:query>
<c:if test="${user.number eq resultSet.rows[0].creator}">
    <sql:update dataSource="jdbc/sarariman">
        DELETE FROM company_events
        WHERE id = ?
        <sql:param value="${param.id}"/>
    </sql:update>
</c:if>
<c:redirect url="../#events"/>