<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<fmt:parseNumber var="project_id" value="${param.project}"/>
<c:set var="project" value="${sarariman.projects.map[project_id]}"/>

<c:if test="${!(sarariman:isCostManager(user, project) || user.administrator)}">
    <jsp:forward page="unauthorized"/>
</c:if>

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
        <title>Weekly Billing Report for ${fn:escapeXml(project.name)}</title>
        <script src="MochiKit/MochiKit.js" type="text/javascript"></script>
        <script src="PlotKit/excanvas.js" type="text/javascript"></script>
        <script src="PlotKit/Base.js" type="text/javascript"></script>
        <script src="PlotKit/Layout.js" type="text/javascript"></script>
        <script src="PlotKit/Canvas.js" type="text/javascript"></script>
        <script src="PlotKit/SweetCanvas.js" type="text/javascript"></script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <c:url var="projectLink" value="project"><c:param name="id" value="${param.project}"/></c:url>

                <h1>Weekly Billing Report for <a href="${projectLink}">${fn:escapeXml(project.name)}</a></h1>

            <div><canvas id="billedChart" width="1000" height="300"></canvas></div>

            <c:set var="weekStarts" value="${du:weekStarts(project.daysBilled)}"/>

            <script type="text/javascript">
                var data = [];
                var xTicks = [];
                // FIXME: Need a way to compute yTicks using max value.
            </script>

            <table id="billing">
                <thead>
                    <tr><th>Week</th><th>Hours</th><th>Billed</th></tr>
                </thead>
                <c:set var="weekNumber" value="0"/>
                <tbody>
                    <c:forEach var="week" items="${weekStarts}">
                        <fmt:formatDate var="formattedWeek" value="${week}" pattern="yyyy-MM-dd"/>
                        <tr>
                            <td>${formattedWeek}</td>
                            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                                SELECT SUM(h.duration) AS durationTotal, SUM(TRUNCATE(c.rate * h.duration + 0.009, 2)) AS costTotal
                                FROM hours AS h
                                JOIN tasks AS t on h.task = t.id
                                JOIN projects AS p on p.id = t.project
                                JOIN labor_category_assignments AS a ON (a.employee = h.employee AND h.date >= a.pop_start AND h.date <= a.pop_end)
                                JOIN labor_categories AS c ON (c.id = a.labor_category AND h.date >= c.pop_start AND h.date <= c.pop_end AND c.project = p.id)
                                WHERE p.id = ? AND t.billable = TRUE AND h.date >= ? and h.date < DATE_ADD(?, INTERVAL 7 DAY);
                                <sql:param value="${param.project}"/>
                                <sql:param value="${week}"/>
                                <sql:param value="${week}"/>
                            </sql:query>

                            <c:forEach var="row" items="${resultSet.rows}">
                                <td class="duration">${row.durationTotal}</td>
                                <td class="currency"><fmt:formatNumber type="currency" value="${row.costTotal}"/></td>
                        <script type="text/javascript">
                                data.push([${weekNumber},${row.costTotal}]);
                                xTicks.push({label: "${formattedWeek}", v: ${weekNumber}});
                        </script>
                    </c:forEach>
                    </tr>
                    <c:set var="weekNumber" value="${weekNumber + 1}"/>
                </c:forEach>
                </tbody>
            </table>

            <script type="text/javascript">
                var layout = new Layout("line", {"xTicks": xTicks} );
                layout.addDataset("billed", data);
                layout.evaluate();

                var chart = new SweetCanvasRenderer($("billedChart"), layout);
                chart.render();
            </script>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
