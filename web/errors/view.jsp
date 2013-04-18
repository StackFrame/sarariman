<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error</title>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <%@include file="../header.jsp" %>

        <h1>Error</h1>

        <fmt:parseNumber var="errorNumber" value="${param.id}"/>
        <c:set var="error" value="${sarariman.errors.map[errorNumber]}"/>

        <pre>
${error.stackTrace}
        </pre>

        <%@include file="../footer.jsp" %>
    </body>
</html>
