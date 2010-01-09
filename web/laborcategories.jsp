<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8" import="com.stackframe.sarariman.Employee"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
        Employee user = (Employee)request.getAttribute("user");
        if (!user.isInvoiceManager()) {
            response.sendError(401);
            return;
        }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Labor Categories</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('categories')">
        <%@include file="header.jsp" %>

        <h1>Labor Categories</h1>

        <div>
            <form method="POST" action="laborcategory">
                <label for="name">Name: </label>
                <input type="text" id="name" name="name"/><br/>

                <label for="project">Project: </label>
                <select id="project" name="project">
                    <sql:query dataSource="jdbc/sarariman" var="projects">
                        SELECT * FROM projects
                    </sql:query>
                    <c:forEach var="project" items="${projects.rows}">
                        <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                        <option value="${project.id}">${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</option>
                    </c:forEach>
                </select><br/>

                <label for="rate">Rate: </label>
                <input type="text" id="rate" name="rate"/><br/>

                <label for="pop_start">Period of Performance Start: </label>
                <input type="text" id="pop_start" name="pop_start"/>

                <label for="pop_end">End: </label>
                <input type="text" id="pop_end" name="pop_end"/><br/>

                <input type="submit" name="create" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if> />
            </form>
        </div>

        <table class="altrows" id="categories">
            <tr><th>Project</th><th>Customer</th><th>Labor Category</th><th>Rate</th><th>Start</th><th>End</th></tr>
            <c:forEach var="entry" items="${sarariman.laborCategories}">
                <tr>
                    <td><a href="laborcategory?id=${entry.id}">${fn:escapeXml(sarariman.projects[entry.project].name)}</a></td>
                    <c:set var="customer" value="${sarariman.customers[sarariman.projects[entry.project].customer]}"/>
                    <td><a href="laborcategory?id=${entry.id}">${fn:escapeXml(customer.name)}</a></td>
                    <td><a href="laborcategory?id=${entry.id}">${entry.name}</a></td>
                    <td class="currency"><a href="laborcategory?id=${entry.id}">$${entry.rate}</a></td>
                    <td><a href="laborcategory?id=${entry.id}">${entry.periodOfPerformanceStart}</a></td>
                    <td><a href="laborcategory?id=${entry.id}">${entry.periodOfPerformanceEnd}</a></td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
