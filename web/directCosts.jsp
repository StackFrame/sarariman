<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<fmt:parseNumber var="projectNumber" value="${param.project}"/>
<c:set var="project" value="${sarariman.projects.map[projectNumber]}"/>
<fmt:parseDate var="start" value="${param.start}" type="date" pattern="yyyy-MM-dd"/>
<fmt:parseDate var="end" value="${param.end}" type="date" pattern="yyyy-MM-dd"/>

<sql:query dataSource="jdbc/sarariman" var="resultSet">
    SELECT project FROM project_managers WHERE employee=? AND project=?
    <sql:param value="${user.number}"/>
    <sql:param value="${project.id}"/>
</sql:query>

<c:set var="isCostManager" value="${sarariman:isCostManager(user, project)}" scope="request"/>

<c:if test="${!(user.administrator || isCostManager)}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!-- This must remain XHTML as we pass it through the flying saucer XHTML renderer.-->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Direct Costs for ${fn:escapeXml(project.name)} (project ${project.id})</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <h1>Direct Costs for ${fn:escapeXml(project.name)} (project ${project.id})</h1>

            <h2>Period of Performance</h2>
            <p>
                Start: <fmt:formatDate value="${start}" type="date" pattern="yyyy-MM-dd"/><br/>
                End: <fmt:formatDate value="${end}" type="date" pattern="yyyy-MM-dd"/>
            </p>

            <h2>Direct Costs</h2>
            <p>
                <%
                    com.stackframe.sarariman.projects.Project project = (com.stackframe.sarariman.projects.Project)pageContext.getAttribute("project");
                    java.util.Date start = (java.util.Date)pageContext.getAttribute("start");
                    java.util.Date end = (java.util.Date)pageContext.getAttribute("end");
                    com.stackframe.sarariman.PeriodOfPerformance pop = com.stackframe.sarariman.PeriodOfPerformance.make(start, end);
                    pageContext.setAttribute("hours", project.getLaborHoursBilled(pop));
                    pageContext.setAttribute("labor", project.getLaborDirectCosts(pop));
                    pageContext.setAttribute("ODC", project.getOtherDirectCosts(pop));
                %>
                Hours <fmt:formatNumber value="${hours}" minFractionDigits="2"/><br/>
                Labor: <fmt:formatNumber type="currency" value="${labor}"/><br/>
                Other Direct Costs: <fmt:formatNumber type="currency" value="${ODC}"/><br/>
                Total: <fmt:formatNumber type="currency" value="${labor + ODC}"/><br/>
            </p>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
