<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<sql:setDataSource dataSource="jdbc/sarariman" var="db"/>
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Timesheets</title>
    </head>
    <!-- FIXME: error if param.week is not a Saturday -->
    <body>
        <p><a href="./">Home</a></p>
        <c:choose>
            <c:when test="${!empty param.week}">
                <fmt:parseDate var="week" value="${param.week}" type="date" pattern="yyyy-MM-dd"/>
            </c:when>
            <c:otherwise>
                <c:set var="week" value="${du:weekStart(du:now())}"/>
            </c:otherwise>
        </c:choose>

        <form action="${request.requestURI}" method="get">
            <fmt:formatDate var="prevWeekString" value="${du:prevWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${prevWeekString}"/>
            <fmt:formatDate var="nextWeekString" value="${du:nextWeek(week)}" type="date" pattern="yyyy-MM-dd"/>
            <input type="submit" name="week" value="${nextWeekString}"/>
        </form>

        <fmt:formatDate var="thisWeekStart" value="${week}" type="date" pattern="yyyy-MM-dd" />

        <c:if test="${sarariman:isAdministrator(user) && !empty param.action}">
            <c:choose>
                <c:when test="${param.action == 'Approve'}">
                    <sql:update dataSource="${db}">
                        UPDATE timecards SET approved=true WHERE date=? AND employee=?
                        <sql:param value="${param.actionWeek}"/>
                        <sql:param value="${param.actionEmployee}"/>
                    </sql:update>
                    <p>Approved timesheet for ${param.actionEmployee} for ${param.actionWeek}.</p>
                </c:when>
                <c:when test="${param.action == 'Reject'}">
                    <sql:update dataSource="${db}">
                        DELETE FROM timecards WHERE date=? AND employee=?
                        <sql:param value="${param.actionWeek}"/>
                        <sql:param value="${param.actionEmployee}"/>
                    </sql:update>
                    <p>Rejected timesheet for ${param.actionEmployee} for ${param.actionWeek}.</p>
                    <!-- FIXME: Make this send the employee an email. -->
                </c:when>
                <c:otherwise>
                    <p class="error">Impossible action!</p>
                </c:otherwise>
            </c:choose>
        </c:if>

        <h2>Timesheets for the week of ${thisWeekStart}</h2>

        <sql:query dataSource="${db}" var="timesheets">
            SELECT * from timecards WHERE date=?
            <sql:param value="${week}"/>
        </sql:query>
        <table>
            <tr><th>Employee</th><th>Approved</th></tr>
            <c:forEach var="timesheet" items="${timesheets.rows}">
                <tr>
                    <td>
                        <c:url var="timesheetLink" value="timesheet">
                            <c:param name="employee" value="${timesheet.employee}"/>
                            <c:param name="week" value="${thisWeekStart}"/>
                        </c:url>
                        <a href="${fn:escapeXml(timesheetLink)}">${directory.employeeMap[timesheet.employee].fullName}</a>
                    </td>
                    <c:set var="approved" value="${timesheet.approved}"/>
                    <td>
                        <form>
                            <input type="checkbox" name="approved" id="approved" disabled="true" <c:if test="${approved}">checked="checked"</c:if>/>
                        </form>
                    </td>
                    <td>
                        <form method="post">
                            <input type="hidden" value="${thisWeekStart}" name="actionWeek"/>
                            <input type="hidden" value="${timesheet.employee}" name="actionEmployee"/>
                            <c:if test="${!approved && sarariman:isAdministrator(user)}">
                                <input type="submit" name="action" value="Approve"/>
                                <input type="submit" name="action" value="Reject"/>
                            </c:if>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>