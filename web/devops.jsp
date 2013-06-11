<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
        <title>DevOps</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>DevOps</h1>

            <p>Server: ${pageContext.servletContext.serverInfo}</p>

            <%
                Map<String, String> systemProperties = new java.util.TreeMap<String, String>();
                String[] propertyNames = new String[]{"java.version", "java.vendor", "java.vendor.url", "java.class.version",
                    "os.name", "os.arch", "os.version"};
                for (String name : propertyNames) {
                    systemProperties.put(name, System.getProperty(name));
                }

                request.setAttribute("systemProperties", systemProperties);
            %>

            <table class="table table-rounded table-striped table-bordered">
                <caption>System Properties</caption>
                <thead>
                    <tr>
                        <th>Property</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${systemProperties}">
                        <tr>
                            <td>${entry.key}</td>
                            <td>${entry.value}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <h2>Services</h2>
            <ul>
                <c:forEach var="service" items="${sarariman.services}">
                    <li>${service}</li>
                </c:forEach>
            </ul>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
