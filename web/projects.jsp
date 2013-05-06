<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

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
        <title>Projects</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <h1>Projects</h1>

            <h2>Create a new project</h2>
            <form method="POST" action="projectController">
                <input type="hidden" name="action" value="create"/>
                <label for="name">Name: </label>
                <input type="text" size="40" id="name" name="name" value="${fn:escapeXml(project.name)}"/><br/>

                <label for="customer">Client: </label>
                <select id="customer" name="customer">
                    <c:forEach var="client" items="${sarariman.clients.all}">
                        <option value="${client.id}">${fn:escapeXml(client.name)}</option>
                    </c:forEach>
                </select><br/>

                <label for="pop_start">Period of Performance Start: </label>
                <input type="text" id="pop_start" name="pop_start"/>

                <label for="pop_end">End: </label>
                <input type="text" id="pop_end" name="pop_end"/><br/>

                <input class="btn" type="submit" name="create" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                </form>
                <br/>

                <table class="table table-bordered table-striped table-rounded" id="projects">
                    <tr><th>ID</th><th>Name</th><th>Customer</th>
                    <c:if test="${user.administrator}"><th>Action</th></c:if>
                    </tr>
                <c:forEach var="project" items="${sarariman.projects.all}">
                    <tr>
                        <td><a href="project?id=${project.id}">${project.id}</a></td>
                        <td><a href="project?id=${project.id}">${fn:escapeXml(project.name)}</a></td>
                        <td><a href="project?id=${project.id}">${fn:escapeXml(project.client.name)}</a></td>
                        <c:if test="${user.administrator}">
                            <td>
                                <form method="POST" action="projectController">
                                    <input type="hidden" name="action" value="delete"/>
                                    <input type="hidden" name="id" value="${project.id}"/>
                                    <button class="btn btn-danger" type="submit" name="delete" value="Delete">
                                        <i class="icon-trash"></i>
                                    </button>
                                </form>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
