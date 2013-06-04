<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Tools</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">

            <ul>
                <li><a href="help"><i class="icon-info-sign"></i> Help</a></li>
                <li><a href="defaultTasks.jsp">Default Task Assignments</a></li>
                <li><a href="tickets/"><i class="icon-tasks"></i> Tickets</a></li>
                <li><a href="orgChart">Org Chart</a></li>
                <li><a href="timesheets">Timesheets</a></li>
                <li><a href="timereportsbyproject">Time reports by project</a></li>
                <li><a href="employees"><i class="icon-group"></i> Employees</a></li>
                <li><a href="scheduledVacation">Scheduled Vacation</a></li>
                <li><a href="stats">Stats</a></li>
                <li><a href="locationlog/everybody.jsp">Location Log Map</a></li>
                <c:if test="${user.administrator}">
                    <li><a href="approvers">Approvers</a></li>
                    <li><a href="globalAudits.jsp">Global Audits</a></li>
                    <li><a href="invoices">Invoices</a></li>
                    <li><a href="invoicemanagers">Invoice Managers</a></li>
                    <li><a href="uninvoicedprojects">Uninvoiced Projects</a></li>
                    <li><a href="customers">Customers</a></li>
                    <li><a href="laborcategoryassignments">Labor Category Assignments</a></li>
                    <li><a href="taskGroupings">Task Groupings</a></li>
                    <li><a href="projects">Projects</a></li>
                    <li><a href="tasks">Tasks</a></li>
                    <li><a href="serviceagreements">Service Agreements</a></li>
                    <li>
                        <fmt:formatDate var="week" value="${du:weekStart(du:now())}" type="date" pattern="yyyy-MM-dd"/>
                        <c:url var="weekBilled" value="weekBilled">
                            <c:param name="week" value="${week}"/>
                        </c:url>
                        <a href="${fn:escapeXml(weekBilled)}">Weekly Billing Report</a>
                    </li>
                    <li><a href="changelog">Changelog</a></li>
                    <li><a href="day">Daily Activity</a></li>
                    <li><a href="contacts">Contacts</a></li>
                </c:if>
                <c:if test="${user.invoiceManager}">
                    <li><a href="laborcategories">Labor Categories</a></li>
                    <li><a href="uninvoicedbillable">Uninvoiced Billable</a></li>
                    <li><a href="saic/">SAIC Tools</a></li>
                    <li><a href="unbilledservices">Unbilled Services</a></li>
                    <li><a href="expenses">Expenses</a></li>
                </c:if>
                <c:if test="${user.benefitsAdministrator}">
                    <li><a href="healthInsuranceSummary.jsp">Current Health Insurance Summary</a></li>
                </c:if>
                <li><a href="accessLog.jsp">Access Log</a></li>
                <li><a href="errors/">Error Log</a></li>
                <li><a href="devops.jsp">DevOps</a></li>
            </ul>
        </div>

        <%@include file="footer.jsp" %>
    </body>
</html>
