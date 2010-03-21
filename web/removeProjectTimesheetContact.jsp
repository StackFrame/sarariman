<%--
  Copyright (C) 2010 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Removed Contact</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <sql:update dataSource="jdbc/sarariman" var="resultSet">
            DELETE FROM project_timesheet_contacts WHERE contact=? AND project=?
            <sql:param value="${param.contact}"/>
            <sql:param value="${param.project}"/>
        </sql:update>

        <h1>Removed Contact</h1>

        <jsp:forward page="project"><jsp:param name="id" value="${param.project}"/></jsp:forward>

        <%@include file="footer.jsp" %>
    </body>
</html>
