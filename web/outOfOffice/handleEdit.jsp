<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- FIXME: Validate begin and end. -->

<sql:update dataSource="jdbc/sarariman">
    UPDATE out_of_office
    SET begin = ?, end = ?, comment = ?
    WHERE id = ?
    <sql:param value="${param.begin}"/>
    <sql:param value="${param.end}"/>
    <sql:param value="${param.comment}"/>
    <sql:param value="${param.id}"/>
</sql:update>
<c:redirect url="../#outOfOffice"/>
