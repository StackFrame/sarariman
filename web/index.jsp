<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="employeeNumber" value="${user.number}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>
        <title>Sarariman</title>

        <!-- jQuery -->
        <link type="text/css" href="jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script src="js/bootstrap.js"></script>

        <!-- TinyMCE -->
        <script type="text/javascript" src="tiny_mce/tiny_mce.js"></script>
        <script type="text/javascript">
            tinyMCE.init({
                mode : "textareas",
                theme : "simple"
            });
        </script>
        <!-- /TinyMCE -->

        <script>
            // FIXME: Replace this with a single function that validates the entire form and enables the submit button if valid.
            function enable(id) {
                var element = document.getElementById(id);
                element.disabled = false;
            }

            $(function() {
                $( "#date" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
        <script>
            function gotCurrentPosition(position) {
                $("input[name='geolocation']").val(position.coords.latitude + "," + position.coords.longitude);
            }

            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(gotCurrentPosition);
            }
        </script>
    </head>

    <!-- FIXME: error if param.week is not a Saturday -->
    <body>
        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <span class="brand">Sarariman</span>

                    <c:set var="good" value="${user.recentEntryLatency < 0.25}"/>
                    <c:choose>
                        <c:when test="${good}"><span class="latencyStatus" title="Your recent timesheet entries have been on time!" style="font-size: 14pt">&#x263A;</span></c:when>
                        <c:otherwise><span class="latencyStatus" title="Your recent timesheet entries have been late." style="font-size: 14pt">&#x2639;</span></c:otherwise>
                    </c:choose>

                    <%@include file="WEB-INF/jspf/userMenu.jspf" %>
                </div>
            </div>
        </div>

        <div class="container-fluid">

            <c:set var="isBoss" value="${sarariman:isBoss(sarariman, user)}"/>

            <c:set var="relatedProjects" value="${user.relatedProjects}"/>

            <ul>
                <c:if test="${isBoss}">
                    <li>
                        Global Audits
                        <ol>
                            <c:forEach var="audit" items="${sarariman.globalAudits}">
                                <c:set var="auditResults" value="${audit.results}"/>
                                <c:if test="${not empty auditResults}">
                                    <li>
                                        ${audit.displayName}
                                        <ol>
                                            <c:forEach var="result" items="${auditResults}">
                                                <li class="error"><a href="${fn:escapeXml(result.URL)}">${result.type}: ${fn:escapeXml(result.message)}</a></li>
                                            </c:forEach>
                                        </ol>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ol>
                    </li>
                </c:if>

                <li>
                    Projects
                    <ol>
                        <c:forEach var="project" items="${relatedProjects}">
                            <li>
                                <c:set var="customer" value="${project.client}"/>
                                <a href="${project.URL}">${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</a>
                                <c:set var="isProjectManager" value="${sarariman:isManager(user, project)}"/>
                                <c:set var="isProjectCostManager" value="${sarariman:isCostManager(user, project)}"/>
                                <c:if test="${isProjectManager or isProjectCostManager}">
                                    <ol>
                                        <c:forEach var="audit" items="${project.audits}">
                                            <c:set var="auditResults" value="${audit.results}"/>
                                            <c:if test="${not empty auditResults}">
                                                <li>
                                                    <c:choose>
                                                        <c:when test="${fn:length(auditResults) == 1}">
                                                            <span class="error"><a href="${auditResults[0].URL}">${auditResults[0].message}</a></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${audit.displayName}
                                                            <ol>
                                                                <c:forEach var="result" items="${auditResults}">
                                                                    <li class="error"><a href="${result.URL}">${result.message}</a></li>
                                                                </c:forEach>
                                                            </ol>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </li>
                                            </c:if>
                                        </c:forEach>
                                    </ol>
                                </c:if>
                            </li>
                        </c:forEach>
                    </ol>
                </li>

                <li>
                    <c:url var="myTicketsURL" value="tickets/">
                        <c:param name="assignee" value="${user.number}"/>
                        <c:param name="notStatus" value="closed"/>
                    </c:url>
                    <a href="${fn:escapeXml(myTicketsURL)}">My Unclosed Tickets</a>
                    <ol>
                        <c:forEach var="ticket" items="${user.unclosedTickets}">
                            <li><a href="${ticket.URL}">${fn:escapeXml(ticket.name)}</a></li>
                        </c:forEach>
                    </ol>
                </li>
            </ul>

            <p><a href="tickets/">All Tickets</a></p>

            <c:choose>
                <c:when test="${!empty param.week}">
                    <fmt:parseDate var="parsedWeek" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
                    <c:set var="week" value="${du:week(parsedWeek)}"/>
                </c:when>
                <c:otherwise>
                    <c:set var="week" value="${du:week(du:now())}"/>
                </c:otherwise>
            </c:choose>

            <c:set var="timesheet" value="${sarariman.timesheets.map[user][week]}"/>
            <c:set var="submitted" value="${timesheet.submitted}"/>

            <c:if test="${!submitted && param.submit}">
                <c:set var="submitted" value="${sarariman:submitTimesheet(timesheet)}"/>
            </c:if>

            <c:if test="${!timesheet.submitted}">
                <div id="recordTime">
                    <h2>Record time worked</h2>
                    <form class="form-horizontal" action="TimesheetEntryHandler" method="post">
                        <div class="control-group">
                            <label class="control-label" for="date">Date</label>
                            <div class="controls">
                                <fmt:formatDate var="now" value="${du:now()}" type="date" pattern="yyyy-MM-dd" />
                                <input size="10" type="text" name="date" id="date" value="${now}"/>
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="task">Task</label>
                            <div class="controls">
                                <select name="task" id="task" onchange="enable('submit');">
                                    <option selected="true"></option>
                                    <c:forEach var="task" items="${user.tasks}">
                                        <option value="${task.id}">${fn:escapeXml(task.name)} (${task.id})
                                            <c:set var="project" value="${task.project}"/>
                                            <c:if test="${!empty project}">
                                                - ${fn:escapeXml(project.name)}:${fn:escapeXml(project.client.name)}
                                            </c:if>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="duration">Duration</label>
                            <div class="controls">
                                <input type="number" step="any" name="duration" id="duration"/>
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="description">Description</label><br/>
                            <div class="controls">
                                <textarea rows="10" name="description" id="description"></textarea>
                            </div>
                        </div>

                        <div class="control-group">
                            <fmt:formatDate var="weekString" value="${week.start.time}" type="date" pattern="yyyy-MM-dd" />
                            <input type="hidden" name="week" value="${weekString}"/><br/>
                            <input type="hidden" id="geolocation" name="geolocation" value=""/>
                            <div class="controls">
                                <input class="btn" type="submit" name="recordTime" value="Record" id="submit" disabled="true"/>
                            </div>
                        </div>
                    </form>
                </div>
            </c:if>

            <div id="weekNavigation">
                <h2>Navigate to another week</h2>
                <form action="${request.requestURI}" method="post">
                    <fmt:formatDate var="prevWeekString" value="${week.previous.start.time}" type="date" pattern="yyyy-MM-dd"/>
                    <input class="btn" type="submit" name="week" value="${prevWeekString}"/>
                    <fmt:formatDate var="nextWeekString" value="${week.next.start.time}" type="date" pattern="yyyy-MM-dd"/>
                    <input class="btn" type="submit" name="week" value="${nextWeekString}"/>
                </form>
            </div>

            <div id="sheetView">
                <fmt:formatDate var="thisWeekStart" value="${week.start.time}" type="date" pattern="yyyy-MM-dd" />

                <h2>Timesheet for the week of ${thisWeekStart}</h2>

                <table id="days" class="table">
                    <c:set var="dayTotals" value="${timesheet.hoursByDay}"/>
                    <caption>Hours by Day</caption>
                    <thead>
                        <tr>
                            <c:forEach items="${dayTotals}" var="entry">
                                <th><fmt:formatDate value="${entry.key.time}" pattern="E"/></th>
                            </c:forEach>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <c:forEach items="${dayTotals}" var="entry">
                                <td>
                                    <c:if test="${entry.value > 0}">
                                        <span class="duration"><fmt:formatNumber value="${entry.value}" minFractionDigits="2"/></span>
                                    </c:if>
                                </td>
                            </c:forEach>
                        </tr>
                    </tbody>
                </table>

                <br/>

                <c:set var="totalHours" value="0.0"/>
                <c:set var="totalRegular" value="0.0"/>
                <c:set var="totalPTO" value="0.0"/>
                <table id="hours" class="table table-striped">
                    <caption>Timesheet Entries</caption>
                    <thead>
                        <tr>
                            <th rowspan="2">Date</th>
                            <th colspan="2">Task</th>
                            <th rowspan="2">Project</th>
                            <th rowspan="2">Customer</th>
                            <th rowspan="2">Duration</th>
                            <th rowspan="2">Description</th>
                            <c:if test="${!timesheet.submitted}">
                                <th rowspan="2"></th>
                            </c:if>
                        </tr>
                        <tr>
                            <th>Name</th>
                            <th>#</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="entry" items="${timesheet.entries}">
                            <tr>
                                <fmt:formatDate var="entryDate" value="${entry.date}" pattern="E, MMM d"/>
                                <td class="date">${entryDate}</td>
                                <td>${fn:escapeXml(entry.task.name)}</td>
                                <td class="task">${entry.task.id}</td>
                                <c:set var="project" value="${entry.task.project}"/>
                                <td>${fn:escapeXml(project.name)}</td>
                                <td>${fn:escapeXml(project.client.name)}</td>
                                <td class="duration">${entry.duration}</td>
                                <c:set var="entryDescription" value="${entry.description}"/>
                                <c:if test="${sarariman:containsHTML(entryDescription)}">
                                    <!-- FIXME: I really only want to escape XML entities in the above fixup. -->
                                    <c:set var="entryDescription" value="${entryDescription}"/>
                                </c:if>
                                <td>${entryDescription}</td>
                                <c:if test="${!timesheet.submitted}">
                                    <td><a class="btn" href="${fn:escapeXml(entry.URL)}">Edit</a></td>
                                </c:if>
                                <c:set var="totalHours" value="${totalHours + entry.duration}"/>
                                <c:choose>
                                    <%-- FIXME: This needs to look this up somewhere. --%>
                                    <c:when test="${entry.task.id == 5}">
                                        <c:set var="totalPTO" value="${totalPTO + entry.duration}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="totalRegular" value="${totalRegular + entry.duration}"/>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="5">Total</td>
                            <td class="duration">${totalHours}</td>
                            <td colspan="2"></td>
                        </tr>
                        <tr>
                            <td colspan="5">Total Regular</td>
                            <td class="duration">${totalRegular}</td>
                            <td colspan="2"></td>
                        </tr>
                        <tr>
                            <td colspan="5">Total PTO</td>
                            <td class="duration">${totalPTO}</td>
                            <td colspan="2"></td>
                        </tr>
                    </tfoot>
                </table>

                <c:set var="hoursNeeded" value="0.0" />
                <c:if test="${user.fulltime}">
                    <c:set var="hoursNeeded" value="40.0" />
                </c:if>
                <c:set var="hoursNeeded" value="${hoursNeeded - totalHours}"/>
                <c:set var="canSubmit" value="true"/>
                <c:if test="${hoursNeeded > 0.0}">
                    <p>Salaried hours remaining in week: <span class="duration">${hoursNeeded}</span></p>
                    <c:set var="canSubmit" value="false"/>
                </c:if>

                <c:if test="${totalHours > 40.0 && totalPTO > 0.0}">
                    <p class="error">PTO taken when sheet is above 40 hours!</p>
                    <c:set var="canSubmit" value="false"/>
                </c:if>

                <form class="form-horizontal" action="${request.requestURI}" method="post">
                    <div class="control-group">
                        <div class="controls">
                            <label class="checkbox" for="submitted">
                                <input type="checkbox" name="submitted" id="submitted" disabled="true" <c:if test="${submitted}">checked="checked"</c:if>/>
                                Submitted
                            </label>
                            <c:set var="approved" value="${timesheet.approved}"/>
                            <label class="checkbox" for="approved">
                                <input type="checkbox" name="approved" id="approved" disabled="true" <c:if test="${approved}">checked="checked"</c:if>/>
                                Approved
                            </label>
                            <c:if test="${!submitted && canSubmit}">
                                <input type="hidden" value="true" name="submit"/>
                                <input class="btn" type="submit" value="Submit"/>
                                <fmt:formatDate var="weekString" value="${week.start.time}" pattern="yyyy-MM-dd"/>
                                <input type="hidden" name="week" value="${weekString}"/>
                            </c:if>
                        </div>
                    </div>
                </form>

            </div>

            <sql:query dataSource="jdbc/sarariman" var="groupingResult">
                SELECT DISTINCT(e.grouping) FROM hours AS h
                JOIN task_grouping_element AS e ON e.task = h.task
                JOIN task_grouping_employee AS emp ON e.grouping = emp.grouping
                WHERE h.duration > 0 AND h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY) AND h.employee = ? AND emp.employee = ?
                <sql:param value="${thisWeekStart}"/>
                <sql:param value="${thisWeekStart}"/>
                <sql:param value="${employeeNumber}"/>
                <sql:param value="${employeeNumber}"/>
            </sql:query>
            <c:if test="${groupingResult.rowCount != 0}">
                <h3>Task Groupings</h3>
                <c:forEach var="groupRow" items="${groupingResult.rows}">
                    <sql:query dataSource="jdbc/sarariman" var="groupResult">
                        SELECT * FROM task_grouping where id=?
                        <sql:param value="${groupRow.grouping}"/>
                    </sql:query>
                    <c:set var="grouping" value="${groupResult.rows[0]}"/>

                    <table>
                        <caption>${fn:escapeXml(grouping.name)}</caption>
                        <tr><th>Task</th><th>Name</th><th>Target</th><th>Actual</th></tr>

                        <sql:query dataSource="jdbc/sarariman" var="elementsResult">
                            SELECT e.fraction, e.task, t.name FROM task_grouping_element AS e
                            JOIN task_grouping AS g ON g.id = e.grouping
                            JOIN tasks AS t ON t.id = e.task
                            WHERE g.id = ?
                            ORDER BY e.task
                            <sql:param value="${grouping.id}"/>
                        </sql:query>

                        <sql:query dataSource="jdbc/sarariman" var="totalActualResult">
                            SELECT SUM(h.duration) AS total FROM hours AS h
                            JOIN task_grouping_element AS e ON e.task = h.task
                            JOIN task_grouping_employee AS emp ON e.grouping = emp.grouping
                            WHERE h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY) AND h.employee = ? AND emp.employee = ?
                            <sql:param value="${thisWeekStart}"/>
                            <sql:param value="${thisWeekStart}"/>
                            <sql:param value="${employeeNumber}"/>
                            <sql:param value="${employeeNumber}"/>
                        </sql:query>
                        <c:set var="totalInGroup" value="${totalActualResult.rows[0].total}"/>

                        <c:forEach var="row" items="${elementsResult.rows}">
                            <tr>
                                <td><a href="task?task_id=${row.task}">${row.task}</a></td>
                                <td><a href="task?task_id=${row.task}">${fn:escapeXml(row.name)}</a></td>
                                <td class="percentage"><fmt:formatNumber value="${row.fraction}" type="percent"/></td>
                                <sql:query dataSource="jdbc/sarariman" var="actualResult">
                                    SELECT SUM(h.duration) AS total FROM hours AS h
                                    WHERE h.date >= ? AND h.date < DATE_ADD(?, INTERVAL 7 DAY) AND h.employee = ? AND h.task = ?
                                    <sql:param value="${thisWeekStart}"/>
                                    <sql:param value="${thisWeekStart}"/>
                                    <sql:param value="${employeeNumber}"/>
                                    <sql:param value="${row.task}"/>
                                </sql:query>
                                <c:set var="actual" value="${actualResult.rows[0].total / totalInGroup}"/>
                                <td class="percentage"><fmt:formatNumber value="${actual}" type="percent"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:forEach>
            </c:if>

            <c:if test="${user.fulltime}">
                <p>
                    PTO available: <span class="duration">${user.paidTimeOff}</span> hours
                    <c:url var="PTOLink" value="PTODetails">
                        <c:param name="employee" value="${employeeNumber}"/>
                    </c:url>
                    <a href="${PTOLink}">history</a>
                </p>
            </c:if>

            <h2 id="events">Events</h2>
            <p>
                <a class="btn" href="events/create.jsp" title="add an event"><i class="icon-plus"></i></a>
            <ul>
                <c:forEach var="event" items="${sarariman.events.current}">
                    <li>
                        <a href="${event.URL}">
                            <c:set var="begin" value="${event.begin}"/>
                            <c:set var="end" value="${event.end}"/>
                            <fmt:formatDate value="${begin}" type="both" dateStyle="long" timeStyle="short" /> -
                            <fmt:formatDate var="beginDate" pattern="yyyy-MM-dd" value="${begin}"/>
                            <fmt:formatDate var="endDate" pattern="yyyy-MM-dd" value="${end}"/>
                            <c:choose>
                                <c:when test="${beginDate eq endDate}">
                                    <fmt:formatDate value="${end}" type="time" timeStyle="short" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${end}" type="both" dateStyle="long" timeStyle="short" />
                                </c:otherwise>
                            </c:choose>
                            - ${fn:escapeXml(event.name)}</a>
                    </li>
                </c:forEach>
            </ul>
        </p>

        <h2>Holidays</h2>
        <p><a href="holidays/upcoming.jsp">Holiday Schedule</a><br/>
            <c:set var="nextHoliday" value="${sarariman.holidays.next}"/>
            Next holiday: <fmt:formatDate value="${nextHoliday.date}" type="date" pattern="MMM d" />, ${nextHoliday.description}</p>

        <h2 id="scheduledVacation">Scheduled Vacation</h2>
        <p>
            <a class="btn" href="vacation/create.jsp" title="add a vacation entry"><i class="icon-plus"></i></a>
        <ul>
            <c:forEach var="entry" items="${user.upcomingVacation}">
                <li>
                    <c:set var="begin" value="${entry.begin}"/>
                    <c:set var="end" value="${entry.end}"/>
                    <c:choose>
                        <c:when test="${begin eq end}">
                            <fmt:formatDate value="${begin}" type="date" dateStyle="long" />
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate value="${begin}" type="date" dateStyle="long" /> -
                            <fmt:formatDate value="${end}" type="date" dateStyle="long" />
                        </c:otherwise>
                    </c:choose>
                    <c:set var="comment" value="${entry.comment}"/>
                    <c:if test="${!empty comment}">
                        - ${fn:escapeXml(comment)}
                    </c:if>
                    <form style="display:inline" method="GET" action="vacation/edit.jsp">
                        <input type="hidden" name="id" value="${entry.id}"/>
                        <button class="btn" type="submit" name="Edit" value="edit" title="edit this entry"><i class="icon-edit"></i></button>
                    </form>
                    <form style="display:inline" method="POST" action="vacation/handleDelete.jsp">
                        <input type="hidden" name="id" value="${entry.id}"/>
                        <button class="btn btn-danger" type="submit" name="Delete" value="delete" title="delete this entry"><i class="icon-trash"></i></button>
                    </form>
                </li>
            </c:forEach>
        </ul>
    </p>

    <h2 id="outOfOffice">Scheduled Out of Office</h2>
    <p>
        <a class="btn" href="outOfOffice/create.jsp" title="add an out of office entry"><i class="icon-plus"></i></a>
    <ul>
        <c:forEach var="entry" items="${user.upcomingOutOfOffice}">
            <li>
                <c:set var="begin" value="${entry.begin}"/>
                <c:set var="end" value="${entry.end}"/>
                <fmt:formatDate value="${begin}" type="both" dateStyle="long" timeStyle="short" /> -
                <fmt:parseDate var="beginDate" pattern="yyyy-MM-dd" value="${begin}"/>
                <fmt:parseDate var="endDate" pattern="yyyy-MM-dd" value="${end}"/>
                <c:choose>
                    <c:when test="${beginDate eq endDate}">
                        <fmt:formatDate value="${end}" type="time" timeStyle="short" />
                    </c:when>
                    <c:otherwise>
                        <fmt:formatDate value="${end}" type="both" dateStyle="long" timeStyle="short" />
                    </c:otherwise>
                </c:choose>
                <c:set var="comment" value="${entry.comment}"/>
                <c:if test="${!empty comment}">
                    - ${fn:escapeXml(comment)}
                </c:if>
                <form style="display:inline" method="GET" action="outOfOffice/edit.jsp">
                    <input type="hidden" name="id" value="${entry.id}"/>
                    <button class="btn" type="submit" name="Edit" value="edit" title="edit this entry"><i class="icon-edit"></i></button>
                </form>
                <form style="display:inline" method="POST" action="outOfOffice/handleDelete.jsp">
                    <input type="hidden" name="id" value="${entry.id}"/>
                    <button class="btn btn-danger" type="submit" name="Delete" value="delete" title="delete this entry"><i class="icon-trash"></i></button>
                </form>
            </li>
        </c:forEach>
    </ul>
</p>

<c:set var="reports" value="${user.reports}"/>
<c:if test="${not empty reports}">
    <h2>Reports</h2>
    <a href="activity">Recent timesheet activity for your reports</a>
    <ol>
        <c:forEach var="report" items="${reports}">
            <li>
                <a href="employee?id=${report.number}">${report.fullName}</a>
                <a href="employee?id=${report.number}"><img class="img-circle" width="25" height="25" onerror="this.style.display='none'" src="${report.photoURL}"/></a>
            </li>
        </c:forEach>
    </ol>
</c:if>

<%@include file="footer.jsp" %>
</div>

</body>
</html>
