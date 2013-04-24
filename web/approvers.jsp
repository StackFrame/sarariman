<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.LinkedHashSet"%>
<%@page import="com.stackframe.sarariman.Directory"%>
<%@page import="com.stackframe.sarariman.Employee"%>
<%@page import="com.stackframe.sarariman.Sarariman" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Approvers</title>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>

            <h1>Approvers</h1>

            <c:set var="isAdministrator" value="${user.administrator}"/>

            <%
                Directory directory = (Directory)getServletContext().getAttribute("directory");
                Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
                LinkedHashSet<Employee> employees = new LinkedHashSet(directory.getByUserName().values());
                employees.removeAll(sarariman.getApprovers());
                request.setAttribute("addableUsers", employees);
            %>

            <form method="POST" action="employeeTableController">
                <input type="hidden" name="action" value="add"/>
                <input type="hidden" name="table" value="approvers"/>
                <select id="employee" name="employee">
                    <c:forEach var="employee" items="${addableUsers}">
                        <option value="${employee.number}">${fn:escapeXml(employee.fullName)}</option>
                    </c:forEach>
                </select>
                <input type="submit" name="add" value="Add" <c:if test="${!isAdministrator}">disabled="true"</c:if> />
                </form>
                <br/>

                <table id="approvers">
                    <tr>
                        <th>Employee</th>
                    <c:if test="${isAdministrator}"><th>Action</th></c:if>
                    </tr>
                <c:forEach var="employee" items="${sarariman.approvers}">
                    <tr>
                        <td>${employee.fullName}</td>
                        <c:if test="${isAdministrator}">
                            <td>
                                <form method="POST" action="employeeTableController">
                                    <input type="hidden" name="action" value="remove"/>
                                    <input type="hidden" name="table" value="approvers"/>
                                    <input type="hidden" name="employee" value="${employee.number}"/>
                                    <input type="submit" name="remove" value="Remove"/>
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
