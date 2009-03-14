<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<c:set var="employeeNumber" value="${user.number}"/>
<sql:setDataSource dataSource="jdbc/sarariman" var="db"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Edit Entry</title>
    </head>
    <body>
        <c:set var="canModify" value="${sarariman:isAdministrator(user) || user.number == param.employee}"/>
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
                <p class="error">Must have a reason for the modification.</p>
                <c:set var="updateError" value="true"/>
            </c:if>

            <c:set var="updateDuration" value="${fn:trim(param.duration)}"/>
            <c:if test="${empty updateDuration}">
                <p class="error">Must have a duration for the modification.</p>
                <c:set var="updateError" value="true"/>
            </c:if>
            <!-- FIXME: Need to also check the duration is a valid number. -->

            <c:if test="${empty updateError}">
                <sql:update dataSource="${db}" var="rowsUpdated">
                    UPDATE hours SET duration=?,description=? WHERE date=? AND employee=? and task=?
                    <sql:param value="${updateDuration}"/>
                    <sql:param value="${updateDescription}"/>
                    <sql:param value="${param.date}"/>
                    <sql:param value="${param.employee}"/>
                    <sql:param value="${param.task}"/>
                </sql:update>
                <c:choose>
                    <c:when test="${rowsUpdated == 1}">
                        <sql:update dataSource="${db}" var="rowsInserted">
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

        <p><a href="./">Home</a></p>
        <sql:query dataSource="${db}" var="entries">
            SELECT hours.task, hours.description, hours.date, hours.duration, tasks.name FROM hours INNER JOIN tasks ON hours.task=tasks.id WHERE employee=? AND hours.date=? AND hours.task=?
            <sql:param value="${param.employee}"/>
            <sql:param value="${param.date}"/>
            <sql:param value="${param.task}"/>
        </sql:query>
        <c:if test="${entries.rowCount != 1}">Did not get the expected row.  rowCount=${rowCount}</c:if>
        <c:set var="entry" value="${entries.rows[0]}"/>
        Employee: ${directory.employeeMap[param.employee].fullName}<br/>
        Date: ${entry.date}<br/>
        Task: ${entry.name}<br/>
        <form action="${request.requestURI}" method="post">
            <input type="hidden" name="date" value="${entry.date}"/>
            <input type="hidden" name="employee" value="${param.employee}"/>
            <input type="hidden" name="task" value="${entry.task}"/>
            <label for="duration">Duration:</label>
            <input size="5" type="text" name="duration" id="duration" value="${entry.duration}"/>
            <br/>
            <label for="description">Description: </label>
            <textarea cols="40" rows="10" name="description" id="description">${entry.description}</textarea><br/>
            <label for="reason">Reason: </label>
            <input size="40" type="text" name="reason" id="reason"/>
            <input type="submit" enabled="${canModify}" name="modifyEntry" value="Modify"/>
        </form>

        <h2>Audit log for this entry</h2>
        <sql:query dataSource="${db}" var="entries">
            SELECT * FROM hours_changelog WHERE task=? and employee=? and date=? ORDER BY timestamp DESC
            <sql:param value="${param.task}"/>
            <sql:param value="${param.employee}"/>
            <sql:param value="${param.date}"/>
        </sql:query>
        <table>
            <tr><th>Timestamp</th><th>Date</th><th>Task #</th><th>Duration</th><th>Employee</th><th>Remote Address</th><th>Remote User</th><th>Reason</th></tr>
            <c:forEach var="entry" items="${entries.rows}">
                <tr>
                    <td>${entry.timestamp}</td>
                    <td>${entry.date}</td>
                    <td>${entry.task}</td>
                    <td>${entry.duration}</td>
                    <td>${directory.employeeMap[entry.employee].userName}</td>
                    <td>${entry.remote_address}</td>
                    <td>${directory.employeeMap[entry.remote_user].userName}</td>
                    <td>${entry.reason}</td>
                </tr>
            </c:forEach>
        </table>

    </body>
</html>