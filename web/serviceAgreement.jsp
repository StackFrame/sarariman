<%--
  Copyright (C) 2011 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <c:set var="agreement_id" value="${param.id}"/>
    <c:if test="${user.administrator}">
        <c:choose>
            <c:when test="${!empty param.update}">
                <sql:update dataSource="jdbc/sarariman">
                    UPDATE service_agreements
                    SET project=?, pop_start=?, pop_end=?, billing_period=?, period_rate=?, description=?
                    WHERE id=?
                    <sql:param value="${param.project}"/>
                    <sql:param value="${param.pop_start}"/>
                    <sql:param value="${param.pop_end}"/>
                    <sql:param value="${param.billing_period}"/>
                    <sql:param value="${param.period_rate}"/>
                    <sql:param value="${param.description}"/>
                    <sql:param value="${agreement_id}"/>
                </sql:update>
            </c:when>
            <c:when test="${!empty param.create}">
                <sql:transaction dataSource="jdbc/sarariman">
                    <sql:update>
                        INSERT INTO service_agreements
                        (project, pop_start, pop_end, billing_period, period_rate, description)
                        VALUES(?, ?, ?, ?, ?, ?);
                        <sql:param value="${param.project}"/>
                        <sql:param value="${param.pop_start}"/>
                        <sql:param value="${param.pop_end}"/>
                        <sql:param value="${param.billing_period}"/>
                        <sql:param value="${param.period_rate}"/>
                        <sql:param value="${param.description}"/>
                    </sql:update>
                    <sql:query var="insertResult">
                        SELECT LAST_INSERT_ID()
                    </sql:query>
                </sql:transaction>
                <c:set var="agreement_id" value="${insertResult.rowsByIndex[0][0]}"/>
            </c:when>
        </c:choose>
    </c:if>

    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Agreement ${agreement_id}</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT *
            FROM service_agreements
            WHERE id=?
            <sql:param value="${agreement_id}"/>
        </sql:query>
        <c:set var="agreement" value="${resultSet.rows[0]}"/>

        <h1>Service Agreement ${agreement_id}</h1>
        <form method="POST">
            <label for="project">Project: </label>
            <select id="project" name="project">
                <option value=""></option>
                <sql:query dataSource="jdbc/sarariman" var="projectResultSet">
                    SELECT * FROM projects
                </sql:query>
                <c:forEach var="row" items="${projectResultSet.rows}">
                    <c:set var="project" value="${sarariman.projects[row.id]}"/>
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                    <option value="${row.id}" <c:if test="${agreement.project == row.id}">selected="selected"</c:if>>${fn:escapeXml(row.name)} - ${fn:escapeXml(customer.name)}</option>
                </c:forEach>
            </select><br/>
            <label for="pop_start">PoP start: </label>
            <input type="text" size="10" id="pop_start" name="pop_start" value="${agreement.pop_start}"/>
            <label for="pop_end">End: </label>
            <input type="text" size="10" id="pop_end" name="pop_end" value="${agreement.pop_end}"/><br/>
            <label for="period_rate">Rate: </label>
            <input type="text" size="7" id="period_rate" name="period_rate" value="${agreement.period_rate}"/><br/>
            <label for="billing_period">Period: </label>
            <select id="billing_period" name="billing_period">
                <option value="monthly" <c:if test="${agreement.billing_period == 'monthly'}">selected="selected"</c:if>>monthly</option>
            </select><br/>
            <label for="description">Description: </label>
            <input type="text" size="80" id="description" name="description" value="${fn:escapeXml(agreement.description)}"/><br/>
            <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>

        <%@include file="footer.jsp" %>
    </body>
</html>
