<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:set var="ticket_id" value="${fn:substring(pageContext.request.pathInfo, 1, -1)}"/>
<fmt:parseNumber var="ticket_number" value="${ticket_id}"/>
<c:set var="ticketBean" value="${sarariman.tickets.map[ticket_number]}"/>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
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
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>Ticket ${ticket_id}: ${fn:escapeXml(ticketBean.name)}</h1>

            <form method="POST" action="TextUpdateHandler">
                <label for="name">Name: </label>
                <input type="hidden" name="ticket" value="${ticket_id}"/>
                <input type="hidden" name="table" value="name"/>
                <input size="50" type="text" id="name" name="text" value="${fn:escapeXml(ticketBean.name)}"/>
                <input type="submit" value="Change Name" name="update"/>
            </form>

            <form method="POST" action="StatusChangeHandler">
                Status: ${ticketBean.status}<br/>
                <select name="status" id="status">
                    <c:forEach var="type" items="${sarariman.tickets.statusTypes}">
                        <c:if test="${type ne ticketBean.status}">
                            <option value="${type}">${type}</option>
                        </c:if>
                    </c:forEach>
                </select>
                <input type="hidden" name="id" value="${ticket_id}"/>
                <input type="submit" value="Change Status"/>
            </form>

            <form method="POST" action="TextUpdateHandler">
                <label for="description">Description: </label>
                <textarea cols="80" rows="10" name="text" id="description">
                    ${fn:escapeXml(ticketBean.description)}
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
                    <c:set var="assignees" value="${ticketBean.assignees}"/>
                    <c:forEach var="e" items="${directory.byUserName}">
                        <c:if test="${e.value.active and !sarariman:contains(assignees, e.value)}">
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

                Creator IP:
                <a href="http://domaintz.com/tools/overview/${ticketBean.creatorIPAddress.hostAddress}">
                    ${ticketBean.creatorIPAddress.hostAddress}
                </a>
                <br/>

                Creator User Agent: ${fn:escapeXml(ticketBean.creatorUserAgent)}
            </p>

            <h2>History</h2>
            <ol>
                <c:forEach var="item" items="${ticketBean.history}">
                    <li>${item.timestamp}: ${item.text}</li>
                </c:forEach>
            </ol>
            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
