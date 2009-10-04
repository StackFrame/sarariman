<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="user" value="${directory.employeeMap[pageContext.request.remoteUser]}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Projects</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows('projects')">
        <p><a href="./">Home</a></p>

        <h1>Projects</h1>

        <h2>Create a new project</h2>
        <form method="POST" action="project">
            <label for="project_name">Name: </label>
            <input type="text" id="project_name" name="project_name" value="${fn:escapeXml(project.name)}"/><br/>
            <label for="project_customer">Customer: </label>
            <select id="project_customer" name="project_customer">
                <sql:query dataSource="jdbc/sarariman" var="customers">
                    SELECT * FROM customers
                </sql:query>
                <c:forEach var="customer" items="${customers.rows}">
                    <option value="${customer.id}" <c:if test="${customer.id == project.customer}">selected="selected"</c:if>>${fn:escapeXml(customer.name)}</option>
                </c:forEach>
            </select><br/>
            <input type="submit" name="create" value="Create" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <br/>

        <table class="altrows" id="projects">
            <tr><th>ID</th><th>Name</th><th>Customer</th></tr>

            <sql:query dataSource="jdbc/sarariman" var="projects">
                SELECT p.id, p.name, p.customer
                FROM projects AS p
            </sql:query>
            <c:forEach var="project" items="${projects.rows}">
                <tr>
                    <td><a href="project?id=${project.id}">${project.id}</a></td>
                    <td><a href="project?id=${project.id}">${fn:escapeXml(project.name)}</a></td>
                    <!-- FIXME: This should be done with a JOIN in SELECT above, but JSTL sql:query does not let us alias column
                    names -->
                    <sql:query dataSource="jdbc/sarariman" var="result" >
                        SELECT name
                        FROM customers
                        WHERE id=?
                        <sql:param value="${project.customer}"/>
                    </sql:query>
                    <c:set var="customer_name" value="${fn:escapeXml(result.rows[0].name)}"/>
                    <td><a href="project?id=${project.id}">${customer_name}</a></td>
                </tr>
            </c:forEach>
        </table>
        <%@include file="footer.jsp" %>
    </body>
</html>
