<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sql:update dataSource="jdbc/sarariman">
    DELETE FROM vacation
    WHERE employee = ? AND begin = ? AND end = ?
    <sql:param value="${user.number}"/>
    <sql:param value="${param.begin}"/>
    <sql:param value="${param.end}"/>
</sql:update>
<c:redirect url="../#scheduledVacation"/>