<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<c:set var="employeeNumber" value="${user.number}"/>
<jsp:useBean beanName="sarariman" id="sarariman" scope="application" type="com.stackframe.sarariman.Sarariman" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Home</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>

    <!-- FIXME: error if param.week is not a Saturday -->
    <body onload="altRows('hours')">
        <a href="tools">Tools</a>

        <c:choose>
            <c:when test="${!empty param.week}">
                <fmt:parseDate var="week" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            </c:when>
            <c:otherwise>
                <c:set var="week" value="${du:weekStart(du:now())}"/>
            </c:otherwise>
        </c:choose>

        <c:set var="timesheet" value="${sarariman:timesheet(sarariman, db, employeeNumber, week)}"/>
        <c:set var="submitted" value="${timesheet.submitted}"/>

        <c:if test="${!submitted && param.submit}">
            <c:set var="submitted" value="${sarariman:submitTimesheet(timesheet)}"/>
        </c:if>

        <c:if test="${!empty param.recordTime}">
            <!-- FIXME: Check that the time is not already in a submitted sheet. -->
            <!-- FIXME: Check that the day is not more than 24 hours on timesheet submit. -->
            <!-- FIXME: Enforce that entry has a comment. -->
            <sql:query dataSource="jdbc/sarariman" var="existing" sql="SELECT * FROM hours WHERE task=? AND date=? AND employee=?">
                <sql:param value="${param.task}"/>
                <sql:param value="${param.date}"/>
                <sql:param value="${employeeNumber}"/>
            </sql:query>
            <c:if test="${!empty existing.rows}">
                <p class="error">Cannot have more than one entry for a given task and date.</p>
                <c:set var="insertError" value="true"/>
            </c:if>

            <c:set var="entryDescription" value="${fn:trim(param.description)}"/>
            <c:if test="${empty entryDescription}">
                <p class="error">You must enter a description.</p>
                <c:set var="insertError" value="true"/>
            </c:if>

            <c:choose>
                <c:when test="${empty param.duration}">
                    <p class="error">You must have a duration.</p>
                    <c:set var="insertError" value="true"/>
                </c:when>
                <c:otherwise>
                    <c:if test="${param.duration <= 0.0}">
                        <p class="error">Duration must be positive.</p>
                        <c:set var="insertError" value="true"/>
                    </c:if>

                    <c:if test="${param.duration > 24.0}">
                        <p class="error">Duration must be less than 24 hours.</p>
                        <c:set var="insertError" value="true"/>
                    </c:if>
                </c:otherwise>
            </c:choose>

            <c:if test="${!insertError}">
                <sql:update dataSource="jdbc/sarariman" var="rowsInserted">
                    INSERT INTO hours (employee, task, date, description, duration) values(?, ?, ?, ?, ?)
                    <sql:param value="${employeeNumber}"/>
                    <sql:param value="${param.task}"/>
                    <sql:param value="${param.date}"/>
                    <sql:param value="${entryDescription}"/>
                    <sql:param value="${param.duration}"/>
                </sql:update>
                <c:choose>
                    <c:when test="${rowsInserted == 1}">
                        <sql:update dataSource="jdbc/sarariman" var="rowsInserted">
                            INSERT INTO hours_changelog (employee, task, date, reason, remote_address, remote_user, duration) values(?, ?, ?, ?, ?, ?, ?)
                            <sql:param value="${employeeNumber}"/>
                            <sql:param value="${param.task}"/>
                            <sql:param value="${param.date}"/>
                            <sql:param value="Entry created."/>
                            <sql:param value="${pageContext.request.remoteHost}"/>
                            <sql:param value="${employeeNumber}"/>
                            <sql:param value="${param.duration}"/>
                        </sql:update>
                        <c:if test="${rowsInserted != 1}">
                            <p class="error">There was an error creating the audit log for your entry.</p>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <p class="error">There was an error creating your entry.</p>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:if>

        <div id="recordTime">
            <h2>Record time worked</h2>
            <form action="${request.requestURI}" method="post">
                <label for="date">Date:</label>
                <fmt:formatDate var="now" value="${du:now()}" type="date" pattern="yyyy-MM-dd" />
                <input size="10" type="text" name="date" id="date" value="${now}"/>
                <label for="task">Task:</label>
                <select name="task" id="task">
                    <sql:setDataSource var="db" dataSource="jdbc/sarariman"/>
                    <c:forEach var="task" items="${sarariman:tasks(db)}">
                        <c:if test="${task.active}">
                            <option value="${task.id}">${fn:escapeXml(task.name)} (${task.id})
                                <c:if test="${!empty task.project}">
                                    - ${fn:escapeXml(task.project.name)}:${fn:escapeXml(task.project.customer.name)}
                                </c:if>
                            </option>
                        </c:if>
                    </c:forEach>
                </select>
                <label for="duration">Duration:</label>
                <input size="5" type="text" name="duration" id="duration"/>
                <br/>
                <label for="description">Description:</label>
                <textarea cols="40" rows="10" name="description" id="description"></textarea>
                <fmt:formatDate var="weekString" value="${week}" type="date" pattern="yyyy-MM-dd" />
                <input type="hidden" name="week" value="${weekString}"/>
                <input type="submit" name="recordTime" value="Record"/>
            </form>
        </div>

        <div id="weekNavigation">
            <h2>Navigate to another week</h2>
            <form action="${request.requestURI}" method="post">
                <fmt:formatDate var="prevWeekString" value="${du:prevWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
                <input type="submit" name="week" value="${prevWeekString}"/>
                <fmt:formatDate var="nextWeekString" value="${du:nextWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
                <input type="submit" name="week" value="${nextWeekString}"/>
            </form>
        </div>

        <div id="sheetView">
            <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

            <h2>Timesheet for the week of ${thisWeekStart}</h2>

            <!-- FIXME: Can I do the nextWeek part in SQL? -->
            <sql:query dataSource="jdbc/sarariman" var="entries">
                SELECT hours.task, hours.description, hours.date, hours.duration, tasks.name
                FROM hours INNER JOIN tasks ON hours.task=tasks.id
                WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY)
                ORDER BY hours.date DESC, hours.task ASC
                <sql:param value="${employeeNumber}"/>
                <sql:param value="${thisWeekStart}"/>
                <sql:param value="${thisWeekStart}"/>
            </sql:query>
            <c:set var="totalHoursWorked" value="0.0"/>
            <table class="altrows" id="hours">
                <tr><th>Date</th><th>Task</th><th>Task #</th><th>Duration</th><th>Description</th><th></th></tr>
                <c:forEach var="entry" items="${entries.rows}">
                    <tr>
                        <fmt:formatDate var="entryDate" value="${entry.date}" pattern="E, MMM d"/>
                        <td class="date">${entryDate}</td>
                        <td>${fn:escapeXml(entry.name)}</td>
                        <td class="task">${entry.task}</td>
                        <td class="duration">${entry.duration}</td>
                        <c:set var="entryDescription" value="${fn:escapeXml(entry.description)}"/>
                        <c:if test="${sarariman:containsHTML(entryDescription)}">
                            <!-- FIXME: I really only want to escape XML entities in the above fixup. -->
                            <c:set var="entryDescription" value="${fn:escapeXml(entryDescription)}"/>
                        </c:if>
                        <td>${entryDescription}</td>
                        <td>
                            <c:url var="editLink" value="editentry">
                                <c:param name="task" value="${entry.task}"/>
                                <c:param name="date" value="${entry.date}"/>
                                <c:param name="employee" value="${employeeNumber}"/>
                            </c:url>
                            <a href="${fn:escapeXml(editLink)}">Edit</a>
                        </td>
                        <c:set var="totalHoursWorked" value="${totalHoursWorked + entry.duration}"/>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="3"><b>Total</b></td>
                    <td class="duration"><b>${totalHoursWorked}</b></td>
                    <td colspan="2"></td>
                </tr>
            </table>

            <c:set var="hoursNeeded" value="0.0" />
            <c:if test="${user.fulltime}">
                <c:set var="hoursNeeded" value="40.0" />
            </c:if>
            <c:set var="hoursNeeded" value="${hoursNeeded - totalHoursWorked}"/>
            <c:if test="${hoursNeeded > 0}">
                <p>Salaried hours remaining in week: <span class="duration">${hoursNeeded}</span></p>
            </c:if>

            <form action="${request.requestURI}" method="post">
                <label for="submitted">Submitted: </label>
                <input type="checkbox" name="submitted" id="submitted" disabled="true" <c:if test="${submitted}">checked="checked"</c:if>/>
                <c:set var="approved" value="${timesheet.approved}"/>
                <label for="approved">Approved: </label>
                <input type="checkbox" name="approved" id="approved" disabled="true" <c:if test="${approved}">checked="checked"</c:if>/>
                <c:if test="${!submitted && hoursNeeded <= 0}">
                    <input type="hidden" value="true" name="submit"/>
                    <input type="submit" value="Submit"/>
                    <fmt:formatDate var="weekString" value="${week}" pattern="yyyy-MM-dd"/>
                    <input type="hidden" name="week" value="${weekString}"/>
                </c:if>
            </form>

        </div>

        <%@include file="footer.jsp" %>
    </body>
</html>
