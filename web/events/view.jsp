<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sql:query dataSource="jdbc/sarariman" var="resultSet">
    SELECT id, begin, end, name, location, location_url, location_map_url, description, creator
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

            Location:
            <c:choose>
                <c:when test="${!empty resultSet.rows[0].location_url}">
                    <a href="${fn:escapeXml(resultSet.rows[0].location_url)}">${fn:escapeXml(resultSet.rows[0].location)}</a>
                </c:when>
                <c:otherwise>
                    ${fn:escapeXml(resultSet.rows[0].location)}
                </c:otherwise>
            </c:choose>

            <c:if test="${!empty resultSet.rows[0].location_map_url}">
                <a href="${fn:escapeXml(resultSet.rows[0].location_map_url)}">map</a>
            </c:if>
            <br/>

            Description: ${fn:escapeXml(resultSet.rows[0].description)}<br/>

            Owner: ${directory.byNumber[resultSet.rows[0].creator].displayName}<br/> <!-- FIXME: Decouple owner from creator so multiple people can manage. -->
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
                <input type="hidden" name="event" value="${resultSet.rows[0].id}"/>
                <input type="hidden" name="employee" value="${user.number}"/>
                <input type="hidden" name="attending" value="true"/>
                <input type="submit" name="Delete" value="I'm attending" <c:if test="${resultRSVP.rows[0].attending eq 'true'}">disabled="true"</c:if>/>
            </form>
            <form style="display:inline" method="POST" action="handleRSVP.jsp">
                <input type="hidden" name="event" value="${resultSet.rows[0].id}"/>
                <input type="hidden" name="employee" value="${user.number}"/>
                <input type="hidden" name="attending" value="false"/>
                <input type="submit" name="Delete" value="I'm not attending" <c:if test="${resultRSVP.rows[0].attending eq 'false'}">disabled="true"</c:if>/>
            </form>
            <form style="display:inline" method="POST" action="handleRSVP.jsp">
                <input type="hidden" name="event" value="${resultSet.rows[0].id}"/>
                <input type="hidden" name="employee" value="${user.number}"/>
                <input type="hidden" name="attending" value="maybe"/>
                <input type="submit" name="Delete" value="I may be attending" <c:if test="${resultRSVP.rows[0].attending eq 'maybe'}">disabled="true"</c:if>/>
            </form>
        </p>

        <p>
            <form style="display:inline" method="GET" action="edit.jsp">
                <input type="hidden" name="id" value="${resultSet.rows[0].id}"/>
                <input type="submit" name="Edit" value="edit" <c:if test="${user.number ne resultSet.rows[0].creator}">disabled="true"</c:if>/>
            </form>
            <form style="display:inline" method="POST" action="handleDelete.jsp">
                <input type="hidden" name="id" value="${resultSet.rows[0].id}"/>
                <input type="submit" name="Delete" value="delete" <c:if test="${user.number ne resultSet.rows[0].creator}">disabled="true"</c:if>/>
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
                    <sql:param value="${employee.value.number}"/>
                </sql:query>
                <c:if test="${resultRSVP.rowCount == 0 and employeeEntry.value.active}">
                    <li>${employeeEntry.value.displayName} 

                        <form style="display:inline" method="POST" action="handleInvitation">
                            <input type="hidden" name="event" value="${param.id}"/>
                            <input type="hidden" name="eventName" value="${fn:escapeXml(resultSet.rows[0].name)}"/>
                            <input type="hidden" name="employee" value="${employeeEntry.value.number}"/>
                            <input type="submit" name="Invite" value="invite" />
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
    </body>
</html>
