<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>SAIC Task ${param.task_id}</title>
    </head>
    <body>
        <p><a href="../">Home</a></p>

        <c:if test="${sarariman:isAdministrator(user) && !empty param.update}">
            <sql:update dataSource="jdbc/sarariman">
                UPDATE saic_tasks
                SET charge_number=?, wbs=?, po_line_item=?
                WHERE task=?
                <sql:param value="${param.charge_number}"/>
                <sql:param value="${param.wbs}"/>
                <sql:param value="${param.po_line_item}"/>
                <sql:param value="${param.task_id}"/>
            </sql:update>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="taskLookup">
            SELECT s.charge_number, t.name, t.id, s.wbs, s.po_line_item
            FROM saic_tasks AS s
            JOIN tasks AS t ON t.id = s.task
            WHERE task=?
            <sql:param value="${param.task_id}"/>
        </sql:query>
        <c:set var="task" value="${taskLookup.rows[0]}"/>

        <h1>Task ${param.task_id}</h1>
        Name: <a href="../task?task_id=${task.id}">${fn:escapeXml(task.name)}</a><br/>
        <form method="POST">
            <label for="charge_number">Charge Number: </label>
            <input type="text" size="19" id="charge_number" name="charge_number" value="${task.charge_number}"/><br/>
            <label for="wbs">WBS: </label>
            <input type="text" id="wbs" name="wbs" value="${task.wbs}"/>
            <label for="po_line_item">PO Line Item: </label>
            <input type="text" size="2" id="po_line_item" name="po_line_item" value="${task.po_line_item}"/><br/>
            <input type="submit" name="update" value="Update" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <%@include file="../footer.jsp" %>
    </body>
</html>
