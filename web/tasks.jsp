<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Tasks</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>

        <sql:query dataSource="jdbc/sarariman" var="tasks">
            SELECT * from tasks
        </sql:query>

        <table>
            <tr><th>ID</th><th>Name</th></tr>
            <c:forEach var="task" items="${tasks.rows}">
                <tr><td>${task.id}</td><td>${fn:escapeXml(task.name)}</td></tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
