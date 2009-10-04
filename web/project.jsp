<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<c:set var="user" value="${directory.byUserName[pageContext.request.remoteUser]}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <c:set var="project_id" value="${param.id}"/>
    <c:if test="${sarariman:isAdministrator(user)}">
        <c:choose>
            <c:when test="${!empty param.update}">
                <sql:update dataSource="jdbc/sarariman">
                    UPDATE projects
                    SET name=?, customer=?
                    WHERE id=?
                    <sql:param value="${param.project_name}"/>
                    <sql:param value="${param.project_customer}"/>
                    <sql:param value="${project_id}"/>
                </sql:update>
            </c:when>
            <c:when test="${!empty param.create}">
                <sql:transaction dataSource="jdbc/sarariman">
                    <sql:update>
                        INSERT INTO projects
                        (name, customer)
                        VALUES(?, ?);
                        <sql:param value="${param.project_name}"/>
                        <sql:param value="${param.project_customer}"/>
                    </sql:update>
                    <sql:query var="insertResult">
                        SELECT LAST_INSERT_ID()
                    </sql:query>
                </sql:transaction>
                <c:set var="project_id" value="${insertResult.rowsByIndex[0][0]}"/>
            </c:when>
        </c:choose>
    </c:if>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Task ${project_id}</title>
    </head>
    <body>
        <p><a href="./">Home</a></p>

        <sql:query dataSource="jdbc/sarariman" var="projectLookup">
            SELECT *
            FROM projects
            WHERE id=?
            <sql:param value="${project_id}"/>
        </sql:query>
        <c:set var="project" value="${projectLookup.rows[0]}"/>

        <h1>Task ${project_id}</h1>
        <form method="POST">
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
            <input type="submit" name="update" value="Update" <c:if test="${!sarariman:isAdministrator(user)}">disabled="true"</c:if> />
        </form>
        <%@include file="footer.jsp" %>
    </body>
</html>
