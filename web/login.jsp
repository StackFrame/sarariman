<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Login</title>
    </head>
    <body onload="document.login.username.focus()">
        <div class="container">
            <h1>Login</h1>
            <c:if test="${authFailed}">
                <p class="text-error">The email address or password you entered was incorrect.</p>
            </c:if>

            <form id="login" name="login" method="POST" action="auth_check">
                <p>
                    <strong>email address: </strong>
                    <input id="username" type="text" name="username" size="40"/>
                </p>
                <p>
                    <strong>password: </strong>
                    <input id="password" type="password" size="25" name="password"/>
                </p>
                <p>
                    <input type="submit" value="Sign In"/>
                </p>
                <input type="hidden" name="destination" value="${param.destination}"/>
            </form>
        </div>
    </body>
</html>
