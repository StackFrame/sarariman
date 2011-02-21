<%--
  Copyright (C) 2011 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Unbilled Services</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Unbilled Services</h1>

        <sql:query dataSource="jdbc/sarariman" var="resultSet">
            SELECT * FROM service_agreements ORDER BY pop_start
        </sql:query>
        <c:forEach var="service_agreement" items="${resultSet.rows}">
            <c:set var="missingBillings" value="${sarariman:missingBillings(sarariman, service_agreement.id)}"/>
            <c:if test="${!empty missingBillings}">
                <h2>
                    <c:set var="project" value="${sarariman.projects[service_agreement.project]}"/>
                    <c:set var="customer" value="${sarariman.customers[project.customer]}"/>
                    ${fn:escapeXml(project.name)} - ${fn:escapeXml(customer.name)}
                </h2>
                <c:forEach var="missingBilling" items="${missingBillings}">
                    <ol>
                        <li>
                            <joda:format value="${missingBilling.popStart}" style="L-" /> - <joda:format value="${missingBilling.popEnd}" style="L-" />
                            <form action="createservicebilling" method="POST">
                                <input type="hidden" name="action" value="create"/>
                                <input type="hidden" name="service_agreement" value="${missingBilling.serviceAgreement}"/>
                                <joda:format var="pop_start" value="${missingBilling.popStart}" pattern="yyyy-MM-dd"/>
                                <input type="hidden" name="pop_start" value="${pop_start}"/>
                                <joda:format var="pop_end" value="${missingBilling.popEnd}" pattern="yyyy-MM-dd"/>
                                <input type="hidden" name="pop_end" value="${pop_end}"/>
                                <input type="submit" name="Bill" value="Bill"/>
                            </form>
                        </li>
                    </ol>
                </c:forEach>
            </c:if>
        </c:forEach>

        <%@include file="footer.jsp" %>
    </body>
</html>
