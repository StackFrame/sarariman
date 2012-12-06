<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:set var="ticket_id" value="${fn:substring(pageContext.request.pathInfo, 1, -1)}"/>

<jsp:useBean id="ticketBean" class="com.stackframe.sarariman.tickets.TicketBean">
    <jsp:setProperty name="ticketBean" property="id" value="${ticket_id}"/>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <title>Ticket ${ticket_id}: ${fn:escapeXml(ticketBean.name)}</title>

        <!-- TinyMCE -->
        <script type="text/javascript" src="../tiny_mce/tiny_mce.js"></script>
        <script type="text/javascript">
            tinyMCE.init({
                mode : "textareas",
                theme : "simple"
            });
        </script>
        <!-- /TinyMCE -->

    </head>
    <body>
        <%@include file="../header.jsp" %>
        <h1>Ticket ${ticket_id}: ${fn:escapeXml(ticketBean.name)}</h1>

        <form method="POST" action="TextUpdateHandler">
            <label for="name">Name: </label>
            <input type="hidden" name="ticket" value="${ticket_id}"/>
            <input type="hidden" name="table" value="name"/>
            <input size="50" type="text" id="name" name="text" value="${fn:escapeXml(ticketBean.name)}"/>
            <input type="submit" value="Change Name" name="update"/>
        </form>

        <p>
            Status: ${ticketBean.status}
            <form method="POST" action="StatusChangeHandler">
                <select name="status" id="status">
                    <sql:query dataSource="jdbc/sarariman" var="statusTypeResultSet">
                        SELECT name FROM ticket_status_type
                    </sql:query>
                    <c:forEach var="row" items="${statusTypeResultSet.rows}">
                        <c:if test="${row.name ne ticketBean.status}">
                            <option value="${row.name}">${row.name}</option>
                        </c:if>
                    </c:forEach>
                </select>
                <input type="hidden" name="id" value="${ticket_id}"/>
                <input type="submit" value="Change Status"/>
            </form>
        </p>

        <form method="POST" action="TextUpdateHandler">
            <label for="description">Description: </label>
            <textarea cols="80" rows="10" name="text" id="description">
                <sql:query dataSource="jdbc/sarariman" var="descriptionResultSet">
                    SELECT description FROM ticket_description WHERE ticket = ?
                    ORDER BY updated DESC
                    LIMIT 1
                    <sql:param value="${ticket_id}"/>
                </sql:query>
                ${fn:escapeXml(descriptionResultSet.rows[0].description)}
            </textarea>
            <input type="hidden" name="ticket" value="${ticket_id}"/>
            <input type="hidden" name="table" value="description"/>
            <input type="submit" value="Update Description" name="update"/>
        </form>

        <h2>Assignees</h2>
        <ol>
            <c:forEach var="assignee" items="${ticketBean.assignees}">
                <li>
                    ${assignee.displayName}
                    <form method="POST" action="AssignmentHandler">
                        <input type="hidden" name="assignee" value="${assignee.number}"/>
                        <input type="hidden" name="id" value="${ticket_id}"/>
                        <input type="hidden" name="assignment" value="-1"/>
                        <input type="submit" value="Unassign"/>
                    </form>
                </li>
            </c:forEach>
        </ol>

        <form method="POST" action="AssignmentHandler">
            <select name="assignee" id="assignee">
                <c:forEach var="e" items="${directory.byUserName}">
                    <!-- FIXME: There must be a smarter way to do this instead of running this query for each employee. -->
                    <sql:query dataSource="jdbc/sarariman" var="sumResultSet">
                        SELECT SUM(assignment) AS sum FROM ticket_assignment WHERE ticket = ? AND assignee = ?
                        <sql:param value="${ticket_id}"/>
                        <sql:param value="${e.value.number}"/>
                    </sql:query>
                    <c:if test="${sumResultSet.rows[0].sum ne 1 and e.value.active}">
                        <option value="${e.value.number}">${e.value.displayName}</option>
                    </c:if>
                </c:forEach>
            </select>
            <input type="hidden" name="id" value="${ticket_id}"/>
            <input type="hidden" name="assignment" value="1"/>
            <input type="submit" value="Add Assignee"/>
        </form>

        <form method="POST" action="TextUpdateHandler">
            <label for="comment">Comment: </label>
            <textarea cols="80" rows="10" name="text" id="comment"></textarea>
            <input type="hidden" name="ticket" value="${ticket_id}"/>
            <input type="hidden" name="table" value="comment"/>
            <input type="submit" value="Add Comment" name="update"/>
        </form>

        <h2>Watchers</h2>
        <ol>
            <c:forEach var="watcher" items="${ticketBean.watchers}">
                <li>${watcher.displayName}
                    <form method="POST" action="WatchHandler">
                        <input type="hidden" name="watcher" value="${watcher.number}"/>
                        <input type="hidden" name="ticket" value="${ticket_id}"/>
                        <input type="hidden" name="watch" value="false"/>
                        <input type="submit" value="Remove"/>
                    </form>
                </li>
            </c:forEach>
        </ol>
        <form method="POST" action="WatchHandler">
            <select name="watcher" id="watcher">
                <c:forEach var="e" items="${directory.byUserName}">
                    <c:if test="${e.value.active and not sarariman:contains(ticketBean.watchers, e.value)}">
                        <option value="${e.value.number}">${e.value.displayName}</option>
                    </c:if>
                </c:forEach>
            </select>
            <input type="hidden" name="ticket" value="${ticket_id}"/>
            <input type="hidden" name="watch" value="true"/>
            <input type="submit" value="Add Watcher"/>
        </form>

        <p>
            Created: ${ticketBean.created}<br/>
            Employee Creator: ${ticketBean.employeeCreator.displayName}<br/>
            <c:if test="${!empty ticketBean.creatorLocation}">
                <c:url var="mapURL" value="https://maps.google.com/maps">
                    <c:param name="q" value="${ticketBean.creatorLocation.latitude},${ticketBean.creatorLocation.longitude}"/>
                </c:url>
                Location: <a href="${mapURL}">${ticketBean.creatorLocation.latitude},${ticketBean.creatorLocation.longitude}</a><br/>
            </c:if>
            Creator IP: ${fn:escapeXml(ticketBean.creatorIPAddress.hostAddress)}<br/>
            Creator User Agent: ${fn:escapeXml(ticketBean.creatorUserAgent)}
        </p>

        <h2>History</h2>
        <ol>
            <c:forEach var="item" items="${ticketBean.history}">
                <li>${item.timestamp}: ${item.text}</li>
            </c:forEach>
        </ol>
        <%@include file="../footer.jsp" %>
    </body>
</html>
