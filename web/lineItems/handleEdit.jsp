<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${!user.administrator}">
    <c:redirect url="../unauthorized"/>
</c:if>

<sql:update dataSource="jdbc/sarariman">
    UPDATE line_items
    SET funded = ?, pop_start = ?, pop_end = ?, description = ?
    WHERE id = ? and project = ?
    <sql:param value="${param.funded}"/>
    <sql:param value="${param.pop_start}"/>
    <sql:param value="${param.pop_end}"/>
    <sql:param value="${param.description}"/>
    <sql:param value="${param.id}"/>
    <sql:param value="${param.project}"/>
</sql:update>
    
<c:url var="projectLink" value="../project">
    <c:param name="id" value="${param.project}"/>
</c:url>
    
<c:redirect url="${projectLink}"/>