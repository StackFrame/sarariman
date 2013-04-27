<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Collection" %>
<%@page import="com.stackframe.sarariman.Sarariman" %>
<%@page import="com.stackframe.sarariman.Employee" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<c:if test="${empty param.employee}">
    <c:redirect url="${pageContext.request.servletPath}">
        <c:param name="employee" value="${user.number}"/>
    </c:redirect>
</c:if>

<%
    Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
    Employee user = (Employee)request.getAttribute("user");
    Collection<Integer> reports = sarariman.getOrganizationHierarchy().getReports(user.getNumber(), true);
    pageContext.setAttribute("reports", reports);
    request.setAttribute("employeeParam", Integer.parseInt(request.getParameter("employee")));
%>

<c:if test="${!(user.number == param.employee || sarariman:contains(reports, employeeParam))}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>PTO Details</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">
            <h1>PTO Details for ${directory.byNumber[param.employee].fullName}</h1>

            <sql:query dataSource="jdbc/sarariman" var="ptoResultSet">
                SELECT SUM(amount) AS total
                FROM paid_time_off
                WHERE employee = ?
                <sql:param value="${param.employee}"/>
            </sql:query>
            <p>Total Available: <span class="duration">${ptoResultSet.rows[0].total}</span> hours</p>

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT * FROM paid_time_off WHERE employee=? ORDER BY effective DESC, created DESC
                <sql:param value="${param.employee}"></sql:param>
            </sql:query>

            <table id="pto">
                <tr><th>Amount</th><th>Comment</th><th>Date</th><th>Added</th></tr>
                <c:forEach var="row" items="${resultSet.rows}">
                    <tr>
                        <td class="duration">${row.amount}</td>
                        <td>${row.comment}</td>
                        <td class="date">${row.effective}</td>
                        <td>${row.created}</td>
                    </tr>
                </c:forEach>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
