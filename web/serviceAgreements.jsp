<%--
  Copyright (C) 2011-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Service Agreement Management</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Service Agreement Management</h1>

        <h2>Create Service Agreement</h2>
        <form method="POST" action="serviceagreement">
            <label for="project">Project: </label>
            <select id="project" name="project">
                <option value=""></option>
                <sql:query dataSource="jdbc/sarariman" var="projectResultSet">
                    SELECT * FROM projects
                </sql:query>
                <c:forEach var="row" items="${projectResultSet.rows}">
                    <c:set var="project" value="${sarariman.projects[row.id]}"/>
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                    <option value="${row.id}">${fn:escapeXml(row.name)} - ${fn:escapeXml(customer.name)}</option>
                </c:forEach>
            </select><br/>
            <label for="pop_start">PoP Start: </label>
            <input type="text" size="10" id="pop_start" name="pop_start" value=""/>
            <label for="pop_end">End: </label>
            <input type="text" size="10" id="pop_end" name="pop_end" value=""/><br/>
            <label for="billing_period">Period: </label>
            <select id="billing_period" name="billing_period">
                <option value="monthly">monthly</option>
            </select><br/>
            <label for="period_rate">Rate: </label>
            <input type="text" size="7" id="period_rate" name="period_rate" value=""/><br/>
            <label for="description">Description: </label>
            <input type="text" size="80" id="description" name="description" value=""/><br/>
            <input type="submit" name="create" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>
        <br/>

        <h2>Service Agreements</h2>
        <table id="tasks">
            <tr>
                <th>Project</th>
                <th>Description</th>
                <th>Period</th>
                <th>Rate</th>
                <th>Start</th>
                <th>End</th>
                <th>Action</th>
            </tr>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT * FROM service_agreements ORDER BY pop_start
            </sql:query>
            <c:forEach var="service_agreement" items="${resultSet.rows}">
                <tr>
                    <c:set var="project" value="${sarariman.projects[service_agreement.project]}"/>
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                    <td>${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</td>
                    <td>${fn:escapeXml(service_agreement.description)}</td>
                    <td>${service_agreement.billing_period}</td>
                    <td class="currency"><fmt:formatNumber type="currency" value="${service_agreement.period_rate}"/></td>
                    <td>${service_agreement.pop_start}</td>
                    <td>${service_agreement.pop_end}</td>
                    <td><a href="serviceagreement?id=${service_agreement.id}" title="edit this service agreement">edit</a></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
