<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<c:set var="employeeNumber" value="${user.number}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Sarariman</title>
        <script type="text/javascript" src="utilities.js"/>

        <!-- jQuery -->
        <link type="text/css" href="jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />	
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

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
    </head>

    <!-- FIXME: error if param.week is not a Saturday -->
    <body onload="altRows()">
        <a href="tools">Tools</a>

        <c:set var="isBoss" value="${sarariman:isBoss(sarariman, user)}"/>

        <p>
            <c:set var="good" value="${user.recentEntryLatency < 0.25}"/>
            <c:choose>
                <c:when test="${good}"><span title="Your recent timesheet entries have been on time!" style="font-size: 14pt">&#x263A;</span></c:when> 
                <c:otherwise><span title="Your recent timesheet entries have been late." style="font-size: 14pt">&#x2639;</span></c:otherwise>
            </c:choose>
        </p>

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
                                            <li class="error">${result.type}: ${result.message}</li>
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
                            <c:url var="projectLink" value="project">
                                <c:param name="id" value="${project.id}"/>
                            </c:url>
                            <a href="${projectLink}">${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</a>
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
                                                        <span class="error">${auditResults[0].message}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${audit.displayName}
                                                        <ol>
                                                            <c:forEach var="result" items="${auditResults}">
                                                                <li class="error">${result.message}</li>
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
                    <c:forEach var="ticket" items="${sarariman.tickets.all}">
                        <c:if test="${sarariman:contains(ticket.assignees, user) and ticket.status ne 'closed'}">
                            <c:url var="ticketViewURL" value="tickets/${ticket.id}"/>
                            <li><a href="${ticketViewURL}">${fn:escapeXml(ticket.name)}</a></li>
                        </c:if>
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

        <c:set var="timesheet" value="${sarariman:timesheet(sarariman, employeeNumber, week)}"/>
        <c:set var="submitted" value="${timesheet.submitted}"/>

        <c:if test="${!submitted && param.submit}">
            <c:set var="submitted" value="${sarariman:submitTimesheet(timesheet)}"/>
        </c:if>

        <c:if test="${!timesheet.submitted}">
            <div id="recordTime">
                <h2>Record time worked</h2>
                <form action="TimesheetEntryHandler" method="post">
                    <label for="date">Date:</label>
                    <fmt:formatDate var="now" value="${du:now()}" type="date" pattern="yyyy-MM-dd" />
                    <input size="10" type="text" name="date" id="date" value="${now}"/>
                    <br/>

                    <label for="task">Task:</label>
                    <select name="task" id="task" onchange="enable('submit');">
                        <option selected="true"></option>
                        <c:forEach var="task" items="${user.tasks}">
                            <option value="${task.id}">${fn:escapeXml(task.name)} (${task.id})
                                <c:if test="${!empty task.project}">
                                    - ${fn:escapeXml(task.project.name)}:${fn:escapeXml(task.project.client.name)}
                                </c:if>
                            </option>
                        </c:forEach>
                    </select>
                    <br/>

                    <label for="duration">Duration:</label>
                    <input size="5" type="text" name="duration" id="duration"/>
                    <br/>
                    <label for="description">Description:</label><br/>
                    <textarea cols="80" rows="10" name="description" id="description"></textarea>
                    <fmt:formatDate var="weekString" value="${week.start.time}" type="date" pattern="yyyy-MM-dd" />
                    <input type="hidden" name="week" value="${weekString}"/><br/>
                    <input type="submit" name="recordTime" value="Record" id="submit" disabled="true"/>
                </form>
            </div>
        </c:if>

        <div id="weekNavigation">
            <h2>Navigate to another week</h2>
            <form action="${request.requestURI}" method="post">
                <fmt:formatDate var="prevWeekString" value="${week.previous.start.time}" type="date" pattern="yyyy-MM-dd"/>
                <input type="submit" name="week" value="${prevWeekString}"/>
                <fmt:formatDate var="nextWeekString" value="${week.next.start.time}" type="date" pattern="yyyy-MM-dd"/>
                <input type="submit" name="week" value="${nextWeekString}"/>
            </form>
        </div>

        <div id="sheetView">
            <fmt:formatDate var="thisWeekStart" value="${week.start.time}" type="date" pattern="yyyy-MM-dd" />

            <h2>Timesheet for the week of ${thisWeekStart}</h2>

            <table class="altrows" id="days">
                <c:set var="dayTotals" value="${timesheet.hoursByDay}"/>
                <tr>
                    <c:forEach items="${dayTotals}" var="entry">
                        <fmt:formatDate var="day" value="${entry.key.time}" pattern="E"/>
                        <th>${day}</th>
                    </c:forEach>
                </tr>
                <tr>
                    <c:forEach items="${dayTotals}" var="entry">
                        <td class="duration">${entry.value}</td>
                    </c:forEach>
                </tr>
            </table>

            <br/>

            <sql:query dataSource="jdbc/sarariman" var="entries">
                SELECT hours.task, hours.description, hours.date, hours.duration, tasks.name AS task_name, projects.name AS project_name, customers.name AS customer_name
                FROM hours INNER JOIN tasks ON hours.task = tasks.id
                LEFT JOIN projects ON projects.id = tasks.project
                LEFT JOIN customers ON customers.id = projects.customer
                WHERE employee=? AND hours.date >= ? AND hours.date < DATE_ADD(?, INTERVAL 7 DAY)
                ORDER BY hours.date DESC, hours.task ASC
                <sql:param value="${employeeNumber}"/>
                <sql:param value="${thisWeekStart}"/>
                <sql:param value="${thisWeekStart}"/>
            </sql:query>
            <c:set var="totalHours" value="0.0"/>
            <c:set var="totalRegular" value="0.0"/>
            <c:set var="totalPTO" value="0.0"/>
            <table class="altrows" id="hours">
                <tr><th rowspan="2">Date</th><th colspan="2">Task</th><th rowspan="2">Project</th><th rowspan="2">Customer</th><th rowspan="2">Duration</th><th rowspan="2">Description</th>
                    <c:if test="${!timesheet.submitted}">
                        <th rowspan="2"></th>
                    </c:if>
                </tr>
                <tr><th>Name</th><th>#</th></tr>
                <c:forEach var="entry" items="${entries.rows}">
                    <tr>
                        <fmt:formatDate var="entryDate" value="${entry.date}" pattern="E, MMM d"/>
                        <td class="date">${entryDate}</td>
                        <td>${fn:escapeXml(entry.task_name)}</td>
                        <td class="task">${entry.task}</td>
                        <td>${fn:escapeXml(entry.project_name)}</td>
                        <td>${fn:escapeXml(entry.customer_name)}</td>
                        <td class="duration">${entry.duration}</td>
                        <c:set var="entryDescription" value="${entry.description}"/>
                        <c:if test="${sarariman:containsHTML(entryDescription)}">
                            <!-- FIXME: I really only want to escape XML entities in the above fixup. -->
                            <c:set var="entryDescription" value="${entryDescription}"/>
                        </c:if>
                        <td>${entryDescription}</td>
                        <c:if test="${!timesheet.submitted}">
                            <td>
                                <c:url var="editLink" value="editentry">
                                    <c:param name="task" value="${entry.task}"/>
                                    <c:param name="date" value="${entry.date}"/>
                                    <c:param name="employee" value="${employeeNumber}"/>
                                </c:url>
                                <a href="${fn:escapeXml(editLink)}">Edit</a>
                            </td>
                        </c:if>
                        <c:set var="totalHours" value="${totalHours + entry.duration}"/>
                        <c:choose>
                            <%-- FIXME: This needs to look this up somewhere. --%>
                            <c:when test="${entry.task == 5}">
                                <c:set var="totalPTO" value="${totalPTO + entry.duration}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="totalRegular" value="${totalRegular + entry.duration}"/>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="5"><b>Total</b></td>
                    <td class="duration"><b>${totalHours}</b></td>
                    <td colspan="2"></td>
                </tr>
                <tr>
                    <td colspan="5"><b>Total Regular</b></td>
                    <td class="duration"><b>${totalRegular}</b></td>
                    <td colspan="2"></td>
                </tr>
                <tr>
                    <td colspan="5"><b>Total PTO</b></td>
                    <td class="duration"><b>${totalPTO}</b></td>
                    <td colspan="2"></td>
                </tr>
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

            <form action="${request.requestURI}" method="post">
                <label for="submitted">Submitted: </label>
                <input type="checkbox" name="submitted" id="submitted" disabled="true" <c:if test="${submitted}">checked="checked"</c:if>/>
                <c:set var="approved" value="${timesheet.approved}"/>
                <label for="approved">Approved: </label>
                <input type="checkbox" name="approved" id="approved" disabled="true" <c:if test="${approved}">checked="checked"</c:if>/>
                <c:if test="${!submitted && canSubmit}">
                    <input type="hidden" value="true" name="submit"/>
                    <input type="submit" value="Submit"/>
                    <fmt:formatDate var="weekString" value="${week}" pattern="yyyy-MM-dd"/>
                    <input type="hidden" name="week" value="${weekString}"/>
                </c:if>
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

                <table class="altrows">
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
            <a href="events/create.jsp">Add an event</a>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT id, begin, end, name, description FROM company_events WHERE (begin >= DATE(NOW()) OR end >= DATE(NOW()))
            </sql:query>
            <c:if test="${resultSet.rowCount != 0}">
                <ul>
                    <c:forEach var="row" items="${resultSet.rows}">
                        <li>
                            <c:url var="eventLink" value="events/view">
                                <c:param name="id" value="${row.id}"/>
                            </c:url>
                            <a href="${eventLink}">
                                <fmt:formatDate value="${row.begin}" type="both" dateStyle="long" timeStyle="short" /> -
                                <fmt:parseDate var="beginDate" pattern="yyyy-MM-dd" value="${row.begin}"/>
                                <fmt:parseDate var="endDate" pattern="yyyy-MM-dd" value="${row.end}"/>
                                <c:choose>
                                    <c:when test="${beginDate eq endDate}">
                                        <fmt:formatDate value="${row.end}" type="time" timeStyle="short" />                                    
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatDate value="${row.end}" type="both" dateStyle="long" timeStyle="short" />                                
                                    </c:otherwise>
                                </c:choose>
                                - ${fn:escapeXml(row.name)}</a>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </p>

        <jsp:useBean id="date" class="java.util.Date" />
        <c:set var="year" value="${date.year + 1900}" />
        <c:url var="holidaysURL" value="holidays.jsp">
            <c:param name="year" value="${year}"/>
        </c:url>
        <h2><a href="${holidaysURL}">Upcoming Holidays</a></h2>
        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT * FROM holidays WHERE date >= DATE(NOW()) AND date < DATE_ADD(NOW(), INTERVAL 4 MONTH) ORDER BY date
        </sql:query>
        <table>
            <tr><th>Date</th><th>Holiday</th></tr>
            <c:forEach var="row" items="${resultSet.rows}">
                <tr>
                    <td><fmt:formatDate value="${row.date}" type="date" pattern="MMM d" /></td>
                    <td>${row.description}</td>
                </tr>
            </c:forEach>
        </table>

        <h2 id="scheduledVacation">Scheduled Vacation</h2>
        <p>
            <a href="vacation/create.jsp">Add an entry</a>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT begin, end, comment FROM vacation WHERE employee=? AND (begin >= DATE(NOW()) OR end >= DATE(NOW()))
                <sql:param value="${employeeNumber}"/>
            </sql:query>
            <c:if test="${resultSet.rowCount != 0}">
                <ul>
                    <c:forEach var="row" items="${resultSet.rows}">
                        <li>
                            <c:choose>
                                <c:when test="${row.begin eq row.end}">
                                    <fmt:formatDate value="${row.begin}" type="date" dateStyle="long" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${row.begin}" type="date" dateStyle="long" /> -
                                    <fmt:formatDate value="${row.end}" type="date" dateStyle="long" />
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${!empty row.comment}">
                                - ${fn:escapeXml(row.comment)}
                            </c:if>
                            <form style="display:inline" method="GET" action="vacation/edit.jsp">
                                <input type="hidden" name="begin" value="${row.begin}"/>
                                <input type="hidden" name="end" value="${row.end}"/>
                                <input type="submit" name="Edit" value="edit"/>
                            </form>
                            <form style="display:inline" method="POST" action="vacation/handleDelete.jsp">
                                <input type="hidden" name="begin" value="${row.begin}"/>
                                <input type="hidden" name="end" value="${row.end}"/>
                                <input type="submit" name="Delete" value="delete"/>
                            </form>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </p>

        <h2 id="outOfOffice">Scheduled Out of Office</h2>
        <p>
            <a href="outOfOffice/create.jsp">Add an entry</a>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT id, begin, end, comment FROM out_of_office WHERE employee=? AND (begin >= DATE(NOW()) OR end >= DATE(NOW()))
                <sql:param value="${employeeNumber}"/>
            </sql:query>
            <c:if test="${resultSet.rowCount != 0}">
                <ul>
                    <c:forEach var="row" items="${resultSet.rows}">
                        <li>
                            <fmt:formatDate value="${row.begin}" type="both" dateStyle="long" timeStyle="short" /> -
                            <fmt:parseDate var="beginDate" pattern="yyyy-MM-dd" value="${row.begin}"/>
                            <fmt:parseDate var="endDate" pattern="yyyy-MM-dd" value="${row.end}"/>
                            <c:choose>
                                <c:when test="${beginDate eq endDate}">
                                    <fmt:formatDate value="${row.end}" type="time" timeStyle="short" />                                    
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${row.end}" type="both" dateStyle="long" timeStyle="short" />                                
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${!empty row.comment}">
                                - ${fn:escapeXml(row.comment)}
                            </c:if>
                            <form style="display:inline" method="GET" action="outOfOffice/edit.jsp">
                                <input type="hidden" name="id" value="${row.id}"/>
                                <input type="submit" name="Edit" value="edit"/>
                            </form>
                            <form style="display:inline" method="POST" action="outOfOffice/handleDelete.jsp">
                                <input type="hidden" name="id" value="${row.id}"/>
                                <input type="submit" name="Delete" value="delete"/>
                            </form>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </p>

        <c:set var="reports" value="${user.reports}"/>
        <c:if test="${not empty reports}">
            <h2>Reports</h2>
            <a href="activity">Recent timesheet activity for your reports</a>
            <ol>
                <c:forEach var="report" items="${reports}">
                    <li><a href="employee?id=${report.number}">${report.fullName}</a></li>
                </c:forEach>
            </ol>
        </c:if>

        <%@include file="footer.jsp" %>
    </body>
</html>
