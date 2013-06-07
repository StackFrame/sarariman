<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../css/style.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>Staff Directory</title>
        <style type="text/css">
            #staff {
                list-style-type: none;
            }
        </style>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>Staff Directory</h1>

            <ul id="staff">
                <c:forEach var="employeeEntry" items="${directory.byUserName}">
                    <c:set var="employee" value="${employeeEntry.value}"/>
                    <c:if test="${employee.active}">
                        <li>
                            <a href="${employee.URL}">
                                <c:choose>
                                    <c:when test="${not empty employee.photo}">
                                        <img class="img-rounded" width="25" height="25"
                                             onerror="this.style.display='none'" src="${employee.photoURL}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <i class="icon-large icon-user"></i>
                                    </c:otherwise>
                                </c:choose>
                            </a>
                            <a href="${employee.URL}">${employee.fullName}</a>
                            ${employee.presence}
                        </li>
                    </c:if>
                </c:forEach>
            </ul>

            <a href="?type=text/vcard" title="download in vCard format"><i class="icon-download"></i> vCard</a>

            <%@include file="/footer.jsp" %>
        </div>
    </body>
</html>
