<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- FIXME: Authorize. -->

<sql:update dataSource="jdbc/sarariman">
    DELETE FROM vacation
    WHERE id = ?
    <sql:param value="${param.id}"/>
</sql:update>
<c:redirect url="../#scheduledVacation"/>