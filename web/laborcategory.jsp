<%--
  Copyright (C) 2009 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <c:set var="id" value="${param.id}"/>

    <c:if test="${user.administrator}">
        <c:choose>
            <c:when test="${!empty param.update}">
                <sql:update dataSource="jdbc/sarariman">
                    UPDATE labor_categories
                    SET name=?, project=?, rate=?, pop_start=?, pop_end=?
                    WHERE id=?
                    <sql:param value="${param.name}"/>
                    <sql:param value="${param.project}"/>
                    <sql:param value="${param.rate}"/>
                    <sql:param value="${param.pop_start}"/>
                    <sql:param value="${param.pop_end}"/>
                    <sql:param value="${param.id}"/>
                </sql:update>
            </c:when>
            <c:when test="${!empty param.create}">
                <sql:transaction dataSource="jdbc/sarariman">
                    <sql:update>
                        INSERT INTO labor_categories
                        (name, project, rate, pop_start, pop_end)
                        VALUES(?, ?, ?, ?, ?);
                        <sql:param value="${param.name}"/>
                        <sql:param value="${param.project}"/>
                        <sql:param value="${param.rate}"/>
                        <sql:param value="${param.pop_start}"/>
                        <sql:param value="${param.pop_end}"/>
                    </sql:update>
                    <sql:query var="insertResult">
                        SELECT LAST_INSERT_ID()
                    </sql:query>
                </sql:transaction>
                <c:set var="id" value="${insertResult.rowsByIndex[0][0]}"/>
            </c:when>
        </c:choose>
    </c:if>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Labor Category ${id}</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <sql:query dataSource="jdbc/sarariman" var="result">
            SELECT *
            FROM labor_categories
            WHERE id=?
            <sql:param value="${id}"/>
        </sql:query>
        <c:set var="laborcategory" value="${result.rows[0]}"/>

        <h1>Labor Category ${id}</h1>
        <form method="POST">
            <label for="name">Name: </label>
            <input type="text" id="name" name="name" value="${fn:escapeXml(laborcategory.name)}"/><br/>

            <label for="project">Project: </label>
            <select id="project" name="project">
                <sql:query dataSource="jdbc/sarariman" var="projects">
                    SELECT * FROM projects
                </sql:query>
                <c:forEach var="project" items="${projects.rows}">
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                    <option value="${project.id}" <c:if test="${labor_category.project == project.id}">selected="selected"</c:if>>${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}</option>
                </c:forEach>
            </select><br/>

            <label for="rate">Rate: </label>
            <input type="text" id="rate" name="rate" value="${laborcategory.rate}"/><br/>

            <label for="pop_start">Period of Performance Start: </label>
            <fmt:formatDate var="pop_start" value="${laborcategory.pop_start}" type="date" pattern="yyyy-MM-dd"/>
            <input type="text" id="pop_start" name="pop_start" value="${pop_start}"/>

            <label for="pop_end">End: </label>
            <fmt:formatDate var="pop_end" value="${laborcategory.pop_end}" type="date" pattern="yyyy-MM-dd"/>
            <input type="text" id="pop_end" name="pop_end" value="${pop_end}"/><br/>

            <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>

        <%@include file="footer.jsp" %>
    </body>
</html>
