<%--
  Copyright (C) 2011 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Expense Management</title>
        <script type="text/javascript" src="utilities.js"/>
    </head>
    <body onload="altRows()">
        <%@include file="header.jsp" %>

        <h1>Expense Management</h1>

        <h2>Create an Expense</h2>
        <form method="POST" action="expense">
            <label for="employee">Employee: </label>
            <select name="employee" id="employee">
                <c:forEach var="e" items="${directory.byUserName}">
                    <option value="${e.value.number}" <c:if test="${e.value.number == employee.number}">selected="selected"</c:if>>${e.value.fullName}</option>
                </c:forEach>
            </select>
            <label for="task">Task: </label>
            <input type="text" id="task" name="task"/><br/>
            <label for="date">Date: </label>
            <input type="text" size="10" id="date" name="date" value="${expense.date}"/><br/>
            <label for="cost">Cost: </label>
            <input type="text" size="7" id="cost" name="cost" value="${expense.cost}"/><br/>
            <label for="description">Description: </label>
            <input type="text" size="80" id="description" name="description" value=""/><br/>
            <input type="submit" name="create" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if> />
        </form>
        <br/>

        <h2>Expenses</h2>
        <table class="altrows" id="tasks">
            <tr><th>ID</th><th>Task</th><th>Description</th><th>Date</th><th>Cost</th></tr>
            <sql:query dataSource="jdbc/sarariman" var="resultSet">
                SELECT * FROM expenses
            </sql:query>
            <c:forEach var="expense" items="${resultSet.rows}">
                <tr>
                    <td><a href="expense?id=${expense.id}">${expense.id}</a></td>
                    <td><a href="expense?id=${expense.id}">${expense.task}</a></td>
                    <td><a href="expense?id=${expense.id}">${fn:escapeXml(expense.description)}</a></td>
                    <td><a href="expense?id=${expense.id}">${expense.date}</a></td>
                    <td><a href="expense?id=${expense.id}">${fn:escapeXml(expense.cost)}</a></td>
                </tr>
            </c:forEach>
        </table>

        <%@include file="footer.jsp" %>
    </body>
</html>
