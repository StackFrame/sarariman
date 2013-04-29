<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:set var="tickets" value="${sarariman.tickets}"/>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../css/style.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>Tickets</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>Tickets</h1>

            <a class="btn" href="create" title="create a ticket"><i class="icon-plus"></i></a>
            <br/>
            <br/>

            <form class="form-inline" method="GET">
                <label for="assignee">Assignee
                    <select name="assignee" id="assignee">
                        <option value="" <c:if test="${empty param.assignee}">selected="selected"</c:if>></option>
                        <c:forEach var="e" items="${directory.byUserName}">
                            <c:if test="${e.value.active}">
                                <option value="${e.value.number}" <c:if test="${param.assignee eq e.value.number}">selected="selected"</c:if>>${e.value.displayName}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </label>

                <label for="status">Status
                    <select name="status" id="status">
                        <option value="" <c:if test="${empty param.status}">selected="selected"</c:if>></option>
                        <c:forEach var="type" items="${tickets.statusTypes}">
                            <option value="${type}" <c:if test="${param.status eq type}">selected="selected"</c:if>>${type}</option>
                        </c:forEach>
                    </select>
                </label>

                <label for="notStatus">Not Status
                    <select name="notStatus" id="notStatus">
                        <option value="" <c:if test="${empty param.notStatus}">selected="selected"</c:if>></option>
                        <c:forEach var="type" items="${tickets.statusTypes}">
                            <option value="${type}" <c:if test="${param.notStatus eq type}">selected="selected"</c:if>>${type}</option>
                        </c:forEach>
                    </select>
                </label>

                <!-- FIXME: Use some JavaScript to make these pickers exclusive. -->

                <button class="btn" type="submit" value="Search" title="search"><i class="icon-search"></i></button>
            </form>

            <table class="table table-striped table-rounded table-bordered">
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Creator</th>
                    <th>Assignee</th>
                </tr>
                <c:forEach var="ticket" items="${tickets.all}">
                    <c:url var="ticketViewURL" value="${ticket.id}"/>

                    <c:set var="skip" value="false"/>

                    <c:if test="${not empty param.status and param.status ne ticket.status}">
                        <c:set var="skip" value="true"/>
                    </c:if>

                    <c:if test="${not empty param.notStatus and param.notStatus eq ticket.status}">
                        <c:set var="skip" value="true"/>
                    </c:if>

                    <c:if test="${not empty param.assignee}">
                        <c:set var="assigneeEmployee" value="${directory.byNumber[param.assignee]}"/>
                        <c:if test="${not sarariman:contains(ticket.assignees, assigneeEmployee)}">
                            <c:set var="skip" value="true"/>
                        </c:if>
                    </c:if>

                    <c:if test="${not skip}">
                        <tr>
                            <td><a href="${ticketViewURL}">${ticket.id}</a></td>
                            <td><a href="${ticketViewURL}">${fn:escapeXml(ticket.name)}</a></td>
                            <td><a href="${ticketViewURL}">${ticket.status}</a></td>
                            <td>${ticket.employeeCreator.displayName}</td>
                            <td>
                                <c:set var="assignees" value="${ticket.assignees}"/>
                                <c:choose>
                                    <c:when test="${fn:length(assignees) == 1}">
                                        ${assignees[0].displayName}
                                    </c:when>
                                    <c:otherwise>
                                        <ul>
                                            <c:forEach var="assignee" items="${assignees}">
                                                <li>${assignee.displayName}</li>
                                            </c:forEach>
                                        </ul>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
