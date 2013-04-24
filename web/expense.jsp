<%--
  Copyright (C) 2011-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!user.invoiceManager}">
    <jsp:forward page="unauthorized"/>
</c:if>

<!DOCTYPE html>
<html>

    <c:set var="expense_id" value="${param.id}"/>
    <c:if test="${user.administrator}">
        <c:choose>
            <c:when test="${!empty param.update}">
                <sql:update dataSource="jdbc/sarariman">
                    UPDATE expenses
                    SET employee=?, task=?, description=?, date=?, cost=?
                    WHERE id=?
                    <sql:param value="${param.employee}"/>
                    <sql:param value="${param.task}"/>
                    <sql:param value="${param.description}"/>
                    <sql:param value="${param.date}"/>
                    <sql:param value="${param.cost}"/>
                    <sql:param value="${expense_id}"/>
                </sql:update>
            </c:when>
            <c:when test="${!empty param.create}">
                <sql:transaction dataSource="jdbc/sarariman">
                    <sql:update>
                        INSERT INTO expenses
                        (employee, task, description, date, cost)
                        VALUES(?, ?, ?, ?, ?);
                        <sql:param value="${param.employee}"/>
                        <sql:param value="${param.task}"/>
                        <sql:param value="${param.description}"/>
                        <sql:param value="${param.date}"/>
                        <sql:param value="${param.cost}"/>
                    </sql:update>
                    <sql:query var="insertResult">
                        SELECT LAST_INSERT_ID()
                    </sql:query>
                </sql:transaction>
                <c:set var="expense_id" value="${insertResult.rowsByIndex[0][0]}"/>
            </c:when>
        </c:choose>
    </c:if>

    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Expense ${expense_id}</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT *
                FROM expenses
                WHERE id=?
                <sql:param value="${expense_id}"/>
            </sql:query>
            <c:set var="expense" value="${resultSet.rows[0]}"/>

            <h1>Expense ${expense_id}</h1>
            <form method="POST">
                <label for="employee">Employee: </label>
                <input type="text" id="employee" name="employee" value="${expense.employee}"/><br/>
                <label for="task">Task: </label>
                <input type="text" id="task" name="task" value="${expense.task}"/><br/>
                <label for="date">Date: </label>
                <input type="text" size="10" id="date" name="date" value="${expense.date}"/><br/>
                <label for="cost">Cost: </label>
                <input type="text" size="7" id="cost" name="cost" value="${expense.cost}"/><br/>
                <label for="description">Description: </label>
                <input type="text" size="80" id="description" name="description" value="${fn:escapeXml(expense.description)}"/><br/>
                <input type="submit" name="update" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if> />
                </form>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
