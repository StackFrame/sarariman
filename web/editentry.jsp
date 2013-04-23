<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!user.administrator && user.number != param.employee}">
    <jsp:forward page="unauthorized"/>
</c:if>

<c:set var="employeeNumber" value="${user.number}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Edit Entry</title>

        <!-- TinyMCE -->
        <script type="text/javascript" src="tiny_mce/tiny_mce.js"></script>
        <script type="text/javascript">
            tinyMCE.init({
                mode : "textareas",
                theme : "simple"
            });
        </script>
        <!-- /TinyMCE -->

    </head>
    <body>
        <%@include file="header.jsp" %>

        <c:set var="canModify" value="${user.administrator || user.number == param.employee}"/>
        <c:if test="${!empty param.modifyEntry}">
            <c:if test="${!canModify}">
                <p class="error">Must be an administrator to change another employee's entries</p>
                <c:set var="updateError" value="true"/>
            </c:if>

            <c:set var="updateDescription" value="${fn:trim(param.description)}"/>
            <c:if test="${empty updateDescription}">
                <p class="error">Must have a description.</p>
                <c:set var="updateError" value="true"/>
            </c:if>

            <c:set var="updateReason" value="${fn:trim(param.reason)}"/>
            <c:if test="${empty updateReason}">
                <fmt:formatDate var="today" value="${du:now()}" type="date" pattern="yyyy-MM-dd"/>
                <c:choose>
                    <c:when test="${today == param.date}">
                        <c:set var="updateReason" value="Modification on same day.  No reason required."/>
                    </c:when>
                    <c:otherwise>
                        <p class="error">Must have a reason for the modification.</p>
                        <c:set var="updateError" value="true"/>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <c:set var="updateDuration" value="${fn:trim(param.duration)}"/>
            <c:if test="${empty updateDuration}">
                <p class="error">Must have a duration for the modification.</p>
                <c:set var="updateError" value="true"/>
            </c:if>
            <!-- FIXME: Need to also check the duration is a valid number. -->

            <c:if test="${empty updateError}">
                <sql:update dataSource="jdbc/sarariman" var="rowsUpdated">
                    UPDATE hours SET duration=?,description=? WHERE date=? AND employee=? and task=?
                    <sql:param value="${updateDuration}"/>
                    <sql:param value="${updateDescription}"/>
                    <sql:param value="${param.date}"/>
                    <sql:param value="${param.employee}"/>
                    <sql:param value="${param.task}"/>
                </sql:update>
                <c:choose>
                    <c:when test="${rowsUpdated == 1}">
                        <sql:update dataSource="jdbc/sarariman" var="rowsInserted">
                            INSERT INTO hours_changelog (employee, task, date, reason, remote_address, remote_user, duration) values(?, ?, ?, ?, ?, ?, ?)
                            <sql:param value="${param.employee}"/>
                            <sql:param value="${param.task}"/>
                            <sql:param value="${param.date}"/>
                            <sql:param value="${updateReason}"/>
                            <sql:param value="${pageContext.request.remoteHost}"/>
                            <sql:param value="${user.number}"/>
                            <sql:param value="${updateDuration}"/>
                        </sql:update>
                        <c:if test="${rowsInserted != 1}">
                            <p class="error">There was an error creating the audit log for the modification.</p>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <p class="error">There was an error modifying the entry.</p>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:if>

        <sql:query dataSource="jdbc/sarariman" var="entries">
            SELECT hours.task, hours.description, hours.date, hours.duration, tasks.name
            FROM hours
            INNER JOIN tasks ON hours.task=tasks.id
            WHERE employee=? AND hours.date=? AND hours.task=?
            <sql:param value="${param.employee}"/>
            <sql:param value="${param.date}"/>
            <sql:param value="${param.task}"/>
        </sql:query>
        <c:if test="${entries.rowCount != 1}">Did not get the expected row.  rowCount=${rowCount}</c:if>
        <c:set var="entry" value="${entries.rows[0]}"/>

        <c:if test="${not empty attemptedOverwrite}">
            <div class="error">
                <p>You attempted to replace an entry for this task with a duration of ${attemptedDuration} and a description of:</p>
                ${attemptedDescription}
                <p>Instead, edit the entry below to take this new data into account.</p>
                <c:set var="attemptedOverwrite" value="" scope="session"/>
                <c:set var="attemptedDuration" value="" scope="session"/>
                <c:set var="attemptedDescription" value="" scope="session"/>
            </div>
        </c:if>

        Employee: ${directory.byNumber[param.employee].fullName}<br/>
        Date: ${entry.date}<br/>
        Task: ${fn:escapeXml(entry.name)} (${entry.task})<br/>
        <form action="${request.requestURI}" method="post">
            <input type="hidden" name="date" value="${entry.date}"/>
            <input type="hidden" name="employee" value="${param.employee}"/>
            <input type="hidden" name="task" value="${entry.task}"/>
            <label for="duration">Duration:</label>
            <input size="5" type="text" name="duration" id="duration" value="${entry.duration}"/>
            <br/>
            <label for="description">Description: </label><br/>
            <textarea cols="80" rows="10" name="description" id="description">${fn:escapeXml(entry.description)}</textarea><br/>
            <label for="reason">Reason: </label>
            <input size="40" type="text" name="reason" id="reason"/>
            <input type="submit" enabled="${canModify}" name="modifyEntry" value="Modify"/>
        </form>

        <h2>Audit log for this entry</h2>
        <sql:query dataSource="jdbc/sarariman" var="entries">
            SELECT * FROM hours_changelog WHERE task=? and employee=? and date=? ORDER BY timestamp DESC
            <sql:param value="${param.task}"/>
            <sql:param value="${param.employee}"/>
            <sql:param value="${param.date}"/>
        </sql:query>
        <table id="entries">
            <tr><th>Timestamp</th><th>Date</th><th>Task #</th><th>Duration</th><th>Employee</th><th>Remote Address</th><th>Remote User</th><th>Reason</th></tr>
            <c:forEach var="entry" items="${entries.rows}">
                <tr>
                    <td>${entry.timestamp}</td>
                    <td>${entry.date}</td>
                    <td>${entry.task}</td>
                    <td>${entry.duration}</td>
                    <td>${directory.byNumber[entry.employee].userName}</td>
                    <td>${entry.remote_address}</td>
                    <td>${directory.byNumber[entry.remote_user].userName}</td>
                    <td>${fn:escapeXml(entry.reason)}</td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
