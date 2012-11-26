<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sql:query dataSource="jdbc/sarariman" var="resultSet">
    SELECT id, begin, end, name, location, description, creator
    FROM company_events
    WHERE id = ?
    <sql:param value="${param.id}"/>
</sql:query>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>${fn:escapeXml(resultSet.rows[0].name)}</title>
    </head>
    <body>
        <%@include file="../header.jsp" %>
        <h1>${fn:escapeXml(resultSet.rows[0].name)}</h1>

        <p>
            Begin: ${resultSet.rows[0].begin}

            End: ${resultSet.rows[0].end}<br/>

            Location: ${fn:escapeXml(resultSet.rows[0].location)}<br/>

            Description: ${fn:escapeXml(resultSet.rows[0].description)}<br/>

            Owner: ${directory.byNumber[resultSet.rows[0].creator].displayName}<br/> <!-- FIXME: Decouple owner from creator so multiple people can manage. -->
        </p>

        <form style="display:inline" method="GET" action="edit.jsp">
            <input type="hidden" name="id" value="${resultSet.rows[0].id}"/>
            <input type="submit" name="Edit" value="edit" <c:if test="${user.number ne resultSet.rows[0].creator}">disabled="true"</c:if>/>
        </form>
        <form style="display:inline" method="POST" action="handleDelete.jsp">
            <input type="hidden" name="id" value="${resultSet.rows[0].id}"/>
            <input type="submit" name="Delete" value="delete" <c:if test="${user.number ne resultSet.rows[0].creator}">disabled="true"</c:if>/>
        </form>

        <%@include file="../footer.jsp" %>
    </body>
</html>
