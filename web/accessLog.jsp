<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Access Log</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">
            <h1>Access Log</h1>
            <h2>Activity for the last 24 hours</h2>

            <p>
                Hits: ${sarariman.accessLog.hitCount}<br/>

                Average time: ${sarariman.accessLog.averageTime}
            </p>

            <table id="worstPerforming" class="table table-striped">
                <caption>Worst Performing Pages</caption>
                <tr>
                    <th>Timestamp</th>
                    <th>Remote Address</th>
                    <th>Employee</th>
                    <th>Status</th>
                    <th>Path</th>
                    <th>Query</th>
                    <th>Method</th>
                    <th>Time</th>
                    <th>User Agent</th>
                </tr>
                <c:forEach var="entry" items="${sarariman.accessLog.longest}">
                    <tr>
                        <td>${entry.timestamp}</td>
                        <td>${entry.remoteAddress}</td>
                        <td>${entry.employee.userName}</td>
                        <td>${entry.status}</td>
                        <c:set var="target" value="${entry.path}"/>
                        <c:if test="${not empty entry.query}">
                            <c:set var="target" value="${target}?${entry.query}"/>
                        </c:if>
                        <td><a href="${target}">${entry.path}</a></td>
                        <td><a href="${target}">${entry.query}</a></td>
                        <td>${entry.method}</td>
                        <td>${entry.time}</td>
                        <td>${entry.userAgent}</td>
                    </tr>
                </c:forEach>
            </table>

            <h3>User Agents</h3>
            <ul>
                <c:forEach var="userAgent" items="${sarariman.accessLog.userAgents}">
                    <li>${userAgent}</li>
                </c:forEach>
            </ul>

            <table id="dayEntries" class="table table-striped">
                <caption>All Entries</caption>
                <tr>
                    <th>Timestamp</th>
                    <th>Remote Address</th>
                    <th>Employee</th>
                    <th>Status</th>
                    <th>Path</th>
                    <th>Query</th>
                    <th>Method</th>
                    <th>Time</th>
                    <th>User Agent</th>
                </tr>
                <c:forEach var="entry" items="${sarariman.accessLog.latest}">
                    <tr>
                        <td>${entry.timestamp}</td>
                        <td>${entry.remoteAddress}</td>
                        <td>${entry.employee.userName}</td>
                        <td>${entry.status}</td>
                        <c:set var="target" value="${entry.path}"/>
                        <c:if test="${not empty entry.query}">
                            <c:set var="target" value="${target}?${entry.query}"/>
                        </c:if>
                        <td><a href="${target}">${entry.path}</a></td>
                        <td><a href="${target}">${entry.query}</a></td>
                        <td>${entry.method}</td>
                        <td>${entry.time}</td>
                        <td>${entry.userAgent}</td>
                    </tr>
                </c:forEach>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
