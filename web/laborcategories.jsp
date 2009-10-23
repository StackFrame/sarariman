<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Labor Categories</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('categories')">
        <p><a href="./">Home</a> <a href="tools">Tools</a></p>
        <h1>Labor Categories</h1>

        <table class="altrows" id="categories">
            <tr><th>Project</th><th>Labor Category</th><th>Rate</th><th>Start</th><th>End</th></tr>
            <c:forEach var="entry" items="${sarariman.laborCategories}">
                <tr>
                    <td><a href="project?id=${entry.project}">${fn:escapeXml(sarariman.projects[entry.project].name)}</a></td>
                    <td>${entry.name}</td>
                    <td class="currency">$${entry.rate}</td>
                    <td>${entry.periodOfPerformanceStart}</td>
                    <td>${entry.periodOfPerformanceEnd}</td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
