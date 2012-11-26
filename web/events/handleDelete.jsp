<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sql:update dataSource="jdbc/sarariman">
    DELETE FROM company_events
    WHERE id = ?
    <sql:param value="${param.id}"/>
</sql:update>
<c:redirect url="../#events"/>