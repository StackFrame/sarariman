<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>Labor Projection</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />    
        <script type="text/javascript" src="../jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script type="text/javascript">            
            $(function() {
                $(".hasDatePicker").datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>
            <h1>Labor Projection</h1>

            <form method="POST">
                <p>
                    <label for="employee">Employee: </label>
                    <select id="employee" name="employee">
                        <c:forEach var="employee" items="${projection.task.project.currentlyAssigned}">
                            <option value="${employee.number}" <c:if test="${employee.number == projection.employee.number}">selected="selected"</c:if>>${employee.displayName}</option>
                        </c:forEach>
                    </select><br/>

                    <label for="task">Task: </label>
                    <select id="task" name="task">
                        <c:forEach var="task" items="${projection.task.project.tasks}">
                            <option value="${task.id}" <c:if test="${task.id == projection.task.id}">selected="selected"</c:if>>${task.id} (${fn:escapeXml(task.name)})</option>
                        </c:forEach>
                    </select><br/>

                    <label for="pop_start">Period of Performance Start: </label>
                    <input type="text" id="start" name="start" value="${projection.periodOfPerformance.start}"
                           class="hasDatePicker"/>

                    <label for="pop_end">End: </label>
                    <input type="text" id="end" name="end" value="${projection.periodOfPerformance.end}"
                           class="hasDatePicker"/><br/>

                    <label for="utilitization">Utilization: </label>
                    <input type="text" id="utilization" name="utilization" value="${projection.utilization}"/><br/>

                    <input type="submit" value="Update" name="update"/>
                </p>
            </form>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
