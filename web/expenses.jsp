<%--
  Copyright (C) 2011-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="du" uri="/WEB-INF/tlds/DateUtils" %>

<c:if test="${!user.invoiceManager}">
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
        <title>Expense Management</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

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
            <table id="tasks" class="table table-bordered table-striped table-rounded">
                <thead>
                    <tr>
                        <th>Task</th>
                        <th>Employee</th>
                        <th>Description</th>
                        <th>Date</th>
                        <th>Cost</th>
                        <th>Invoice</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <sql:query dataSource="jdbc/sarariman" var="resultSet">
                        SELECT * FROM expenses ORDER BY date
                    </sql:query>
                    <c:forEach var="expense" items="${resultSet.rows}">
                        <tr>
                            <td><a href="task?task_id=${expense.task}" title="view task ${expense.task}">${expense.task}</a></td>
                            <td><a href="employee?id=${expense.employee}" title="view employee info for ${directory.byNumber[expense.employee].fullName}">${directory.byNumber[expense.employee].fullName}</a></td>
                            <td>${fn:escapeXml(expense.description)}</td>
                            <td>${expense.date}</td>
                            <td>${fn:escapeXml(expense.cost)}</td>
                            <td>
                                <c:if test="${!empty expense.invoice}">
                                    <a href="invoice?invoice=${expense.invoice}" title="view invoice ${expense.invoice}">${expense.invoice}</a>
                                </c:if>
                            </td>
                            <td><a href="expense?id=${expense.id}" title="edit this expense">edit</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
