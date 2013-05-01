<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Collection"%>
<%@page import="com.stackframe.sarariman.Sarariman"%>
<%@page import="com.stackframe.sarariman.Employee"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>
    <c:set var="employee" value="${directory.byNumber[param.id]}"/>
    <head>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>${employee.fullName}</title>

        <script>
            $(document).ready(function(){
                $('.delete_task_assignment').click(function(e) {
                    $.ajax({
                        type: 'DELETE',
                        url: 'taskassignment?' + $(this).attr('data'),
                        async: false,
                        success: function() {
                            location.reload();
                        }
                    });
                });

                $('#add_task_assignment_button').click(function(e) {
                    $.ajax({
                        type: 'POST',
                        url: 'taskassignment',
                        data: $(this).attr('data') + "&task=" + $('#add_task_assignment').val(),
                        async: false,
                        success: function() {
                            location.reload();
                        }
                    });
                });
            });
        </script>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>
        <div class="container-fluid">
            <h1>${employee.fullName}</h1>
            <img class="img-rounded pull-right" id="photo" width="100" height="100" onerror="this.style.display='none'" src="${employee.photoURL}"/>

            <%
                Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
                Employee employee = (Employee)pageContext.getAttribute("employee");
                Collection<Integer> managers = sarariman.getOrganizationHierarchy().getManagers(employee.getNumber());
                pageContext.setAttribute("managers", managers);
            %>
            <c:if test="${!empty managers}">
                <h2>Managers</h2>
                <ul>
                    <c:forEach var="manager" items="${managers}">
                        <c:url var="managerURL" value="${pageContext.request.servletPath}"><c:param name="id" value="${manager}"/></c:url>
                        <li><a href="${managerURL}">${directory.byNumber[manager].fullName}</a></li>
                    </c:forEach>
                </ul>
            </c:if>

            <c:set var="assistants" value="${employee.administrativeAssistants}"/>
            <c:if test="${not empty assistants}">
                <h2>Administrative Assistants</h2>
                <ul>
                    <c:forEach var="assistant" items="${assistants}">
                        <c:url var="link" value="employee">
                            <c:param name="id" value="${assistant.number}"/>
                        </c:url>
                        <li><a href="${link}">${assistant.displayName}</a></li>
                    </c:forEach>
                </ul>
            </c:if>

            <%
                Collection<Integer> directReports = sarariman.getOrganizationHierarchy().getDirectReports(employee.getNumber());
                pageContext.setAttribute("directReports", directReports);
            %>
            <c:if test="${!empty directReports}">
                <h2>Direct Reports</h2>
                <ul>
                    <c:forEach var="report" items="${directReports}">
                        <c:url var="reportURL" value="${pageContext.request.servletPath}"><c:param name="id" value="${report}"/></c:url>
                        <li><a href="${reportURL}">${directory.byNumber[report].fullName}</a></li>
                    </c:forEach>
                </ul>
            </c:if>

            <!-- FIXME: Need a section with labor category assignments. -->

            <form action="employeeController" method="POST">
                <input type="hidden" name="employee" value="${employee.number}"/>
                <input type="hidden" name="action" value="setAdministrator"/>
                <label for="administrator">Administrator
                    <input type="checkbox" id="administrator" name="administrator"
                           <c:if test="${employee.administrator}">checked="checked"</c:if>
                           <c:if test="${not user.administrator}">disabled="true"</c:if>
                           onchange="this.form.submit();"/>
                </label>
            </form>
            <form action="employeeController" method="POST">
                <input type="hidden" name="employee" value="${employee.number}"/>
                <input type="hidden" name="action" value="setBenefitsAdministrator"/>
                <label for="administrator">Benefits Administrator
                    <input type="checkbox" id="administrator" name="administrator"
                           <c:if test="${employee.benefitsAdministrator}">checked="checked"</c:if>
                           <c:if test="${not user.administrator}">disabled="true"</c:if>
                           onchange="this.form.submit();"/>
                </label>
            </form>
            <form action="employeeController" method="POST">
                <input type="hidden" name="employee" value="${employee.number}"/>
                <input type="hidden" name="action" value="setPayrollAdministrator"/>
                <label for="administrator">Payroll Administrator
                    <input type="checkbox" id="administrator" name="administrator"
                           <c:if test="${employee.payrollAdministrator}">checked="checked"</c:if>
                           <c:if test="${not user.administrator}">disabled="true"</c:if>
                           onchange="this.form.submit();"/>
                </label>
            </form>
            <br/>

            <c:if test="${user == employee or user.administrator}">
                Birthdate: <joda:format value="${employee.birthdate}" style="L-" /><br/>
                Age: ${employee.age}<br/>
            </c:if>
            <c:if test="${user == employee or user.payrollAdministrator}">
                Hourly pay rate: <fmt:formatNumber type="currency" value="${employee.directRate}"/><br/>
                <c:if test="${employee.fulltime}">
                    Salary: <fmt:formatNumber type="currency" value="${employee.directRate * 2080}"/><br/>
                    Vacation: <fmt:formatNumber type="currency" value="${employee.directRate * 10 * 8}"/><br/>
                    Sick: <fmt:formatNumber type="currency" value="${employee.directRate * 12 * 8}"/><br/>
                    Holiday: <fmt:formatNumber type="currency" value="${employee.directRate * 9 * 8}"/><br/>
                    Health insurance: <fmt:formatNumber type="currency" value="${employee.monthlyHealthInsurancePremium * 12}"/><br/>
                </c:if>
            </c:if>

            <ul>
                <c:if test="${user == employee or user.payrollAdministrator}">
                    <li>
                        <c:url var="PTOLink" value="PTODetails">
                            <c:param name="employee" value="${param.id}"/>
                        </c:url>
                        <a href="${PTOLink}">Paid Time Off</a>
                    </li>
                </c:if>
                <li>
                    <c:url var="myTicketsURL" value="tickets/">
                        <c:param name="assignee" value="${param.id}"/>
                        <c:param name="notStatus" value="closed"/>
                    </c:url>
                    <a href="${fn:escapeXml(myTicketsURL)}">Unclosed Tickets</a>
                </li>
                <li>
                    <c:url var="taskAssignmentsURL" value="taskAssignments.jsp">
                        <c:param name="employee" value="${param.id}"/>
                    </c:url>
                    <a href="${taskAssignmentsURL}">Task Assignments</a>
                </li>
            </ul>

            <c:if test="${user.administrator}">
                <h2>Direct Rate</h2>
                <sql:query dataSource="jdbc/sarariman" var="resultSet">
                    SELECT rate, effective
                    FROM direct_rate
                    WHERE employee=?
                    ORDER BY effective DESC
                    <sql:param value="${param.id}"/>
                </sql:query>
                <table class="table table-striped table-bordered">
                    <thead>
                        <tr><th>Rate</th><th>Effective Date</th><th>Duration</th></tr>
                    </thead>
                    <tbody>
                        <c:set var="endDate" value="${du:now()}"/>
                        <c:forEach var="rate_row" items="${resultSet.rows}" varStatus="varStatus">
                            <tr class="${varStatus.index % 2 == 0 ? 'evenrow' : 'oddrow'}">
                                <td class="currency"><fmt:formatNumber type="currency" value="${rate_row.rate}"/></td>
                                <fmt:parseDate var="effective" value="${rate_row.effective}" pattern="yyyy-MM-dd"/>
                                <td><fmt:formatDate value="${effective}" pattern="yyyy-MM-dd"/></td>
                                <td>${du:daysBetween(effective, endDate)} days</td>
                                <c:set var="endDate" value="${effective}"/>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT begin, end, comment FROM vacation WHERE employee=? AND (begin >= DATE(NOW()) OR end >= DATE(NOW()))
                <sql:param value="${param.id}"/>
            </sql:query>
            <c:if test="${resultSet.rowCount != 0}">
                <h2>Scheduled Vacation</h2>
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
                            ${fn:escapeXml(row.comment)}
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <c:set var="oof" value="${employee.upcomingOutOfOffice}"/>
            <c:if test="${not empty oof}">
                <h2>Scheduled Out of Office</h2>
                <ul>
                    <c:forEach var="entry" items="${oof}">
                        <li>
                            <fmt:formatDate value="${entry.begin}" type="both" dateStyle="long" timeStyle="short" /> -
                            <fmt:parseDate var="beginDate" pattern="yyyy-MM-dd" value="${entry.begin}"/>
                            <fmt:parseDate var="endDate" pattern="yyyy-MM-dd" value="${entry.end}"/>
                            <c:choose>
                                <c:when test="${beginDate eq endDate}">
                                    <fmt:formatDate value="${entry.end}" type="time" timeStyle="short" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${entry.end}" type="both" dateStyle="long" timeStyle="short" />
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${!empty entry.comment}">
                                - ${fn:escapeXml(entry.comment)}
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT project FROM project_managers WHERE employee=?
                <sql:param value="${param.id}"/>
            </sql:query>
            <c:if test="${resultSet.rowCount != 0}">
                <h2>Projects Managed</h2>
                <ul>
                    <c:forEach var="mapping_row" items="${resultSet.rows}">
                        <c:set var="project" value="${sarariman.projects.map[mapping_row.project]}"/>
                        <c:url var="link" value="project">
                            <c:param name="id" value="${mapping_row.project}"/>
                        </c:url>
                        <li><a href="${link}">${fn:escapeXml(project.name)} - ${fn:escapeXml(project.client.name)}</a></li>
                    </c:forEach>
                </ul>
            </c:if>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
