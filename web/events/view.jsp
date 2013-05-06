<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sql:query dataSource="jdbc/sarariman" var="eventResultSet">
    SELECT id, begin, end, name, location, location_url, location_map_url, description, creator
    FROM company_events
    WHERE id = ?
    <sql:param value="${param.id}"/>
</sql:query>
<c:set var="event" value="${eventResultSet.rows[0]}"/>

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
        <title>${fn:escapeXml(event.name)}</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>${fn:escapeXml(event.name)}</h1>

            <p>
                Begin: ${event.begin}

                End: ${event.end}<br/>

                Location:
                <c:choose>
                    <c:when test="${!empty event.location_url}">
                        <a href="${fn:escapeXml(event.location_url)}">${fn:escapeXml(event.location)}</a>
                    </c:when>
                    <c:otherwise>
                        ${fn:escapeXml(event.location)}
                    </c:otherwise>
                </c:choose>

                <c:if test="${!empty event.location_map_url}">
                    <a href="${fn:escapeXml(event.location_map_url)}">map</a>
                </c:if>
                <br/>

                Description: ${fn:escapeXml(event.description)}<br/>

                Owner: ${directory.byNumber[event.creator].displayName}<br/> <!-- FIXME: Decouple owner from creator so multiple people can manage. -->
            </p>

            <p>
                <sql:query dataSource="jdbc/sarariman" var="resultRSVP">
                    SELECT attending
                    FROM company_events_rsvp
                    WHERE event = ? AND employee = ?
                    <sql:param value="${param.id}"/>
                    <sql:param value="${user.number}"/>
                </sql:query>
            <form style="display:inline" method="POST" action="handleRSVP.jsp">
                <input type="hidden" name="event" value="${event.id}"/>
                <input type="hidden" name="employee" value="${user.number}"/>
                <input type="hidden" name="attending" value="true"/>
                <input class="btn" type="submit" name="Delete" value="I'm attending" <c:if test="${resultRSVP.rows[0].attending eq 'true'}">disabled="true"</c:if>/>
                </form>
                <form style="display:inline" method="POST" action="handleRSVP.jsp">
                    <input type="hidden" name="event" value="${event.id}"/>
                <input type="hidden" name="employee" value="${user.number}"/>
                <input type="hidden" name="attending" value="false"/>
                <input class="btn" type="submit" name="Delete" value="I'm not attending" <c:if test="${resultRSVP.rows[0].attending eq 'false'}">disabled="true"</c:if>/>
                </form>
                <form style="display:inline" method="POST" action="handleRSVP.jsp">
                    <input type="hidden" name="event" value="${event.id}"/>
                <input type="hidden" name="employee" value="${user.number}"/>
                <input type="hidden" name="attending" value="maybe"/>
                <input class="btn" type="submit" name="Delete" value="I may be attending" <c:if test="${resultRSVP.rows[0].attending eq 'maybe'}">disabled="true"</c:if>/>
                </form>
            </p>

            <p>
            <form style="display:inline" method="GET" action="edit.jsp">
                <input type="hidden" name="id" value="${event.id}"/>
            <button class="btn" type="submit" name="Edit" value="edit"
                    <c:if test="${user.number ne event.creator}">disabled="true"</c:if>><i class="icon-edit"></i></button>
            </form>
            <form style="display:inline" method="POST" action="handleDelete.jsp">
                <input type="hidden" name="id" value="${event.id}"/>
            <button class="btn btn-danger" type="submit" name="Delete" value="delete"
                    <c:if test="${user.number ne event.creator}">disabled="true"</c:if>><i class="icon-trash"></i></button>
            </form>
        </p>

        <h2>Attending</h2>
    <sql:query dataSource="jdbc/sarariman" var="resultAttending">
        SELECT employee
        FROM company_events_rsvp
        WHERE event = ? AND attending = "true"
        <sql:param value="${param.id}"/>
    </sql:query>
    <ol>
        <c:forEach var="row" items="${resultAttending.rows}">
            <li>${directory.byNumber[row.employee].displayName}</li>
        </c:forEach>
    </ol>

    <h2>May Be Attending</h2>
    <sql:query dataSource="jdbc/sarariman" var="resultAttending">
        SELECT employee
        FROM company_events_rsvp
        WHERE event = ? AND attending = "maybe"
        <sql:param value="${param.id}"/>
    </sql:query>
    <ol>
        <c:forEach var="row" items="${resultAttending.rows}">
            <li>${directory.byNumber[row.employee].displayName}</li>
        </c:forEach>
    </ol>

    <h2>Not Attending</h2>
    <sql:query dataSource="jdbc/sarariman" var="resultAttending">
        SELECT employee
        FROM company_events_rsvp
        WHERE event = ? AND attending = "false"
        <sql:param value="${param.id}"/>
    </sql:query>
    <ol>
        <c:forEach var="row" items="${resultAttending.rows}">
            <li>${directory.byNumber[row.employee].displayName}</li>
        </c:forEach>
    </ol>

    <h2>No Response</h2>
    <ol>
        <c:forEach var="employeeEntry" items="${directory.byUserName}">
            <sql:query dataSource="jdbc/sarariman" var="resultRSVP">
                SELECT employee
                FROM company_events_rsvp
                WHERE event = ? AND employee = ?
                <sql:param value="${param.id}"/>
                <sql:param value="${employeeEntry.value.number}"/>
            </sql:query>
            <c:if test="${resultRSVP.rowCount == 0 and employeeEntry.value.active}">
                <li>${employeeEntry.value.displayName}

                    <form style="display:inline" method="POST" action="handleInvitation">
                        <input type="hidden" name="event" value="${param.id}"/>
                        <input type="hidden" name="eventName" value="${fn:escapeXml(event.name)}"/>
                        <input type="hidden" name="employee" value="${employeeEntry.value.number}"/>
                        <input class="btn" type="submit" name="Invite" value="invite" <c:if test="${user.number ne event.creator}">disabled="true"</c:if>/>
                        </form>

                    <sql:query dataSource="jdbc/sarariman" var="resultLog">
                        SELECT invited
                        FROM company_events_invitation_log
                        WHERE event = ? AND employee = ?
                        ORDER BY invited DESC
                        <sql:param value="${param.id}"/>
                        <sql:param value="${employeeEntry.value.number}"/>
                    </sql:query>

                    <c:if test="${resultLog.rowCount ne 0}">
                        Invited ${resultLog.rows[0].invited}.
                    </c:if>
                </li>
            </c:if>
        </c:forEach>
    </ol>

    <%@include file="../footer.jsp" %>
</div>
</body>
</html>
