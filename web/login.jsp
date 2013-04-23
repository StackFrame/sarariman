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
        <title>Login</title>
        <style type="text/css">
            body {
                margin: 0px auto;
                padding: 15px;
                background: white;
                color: black;
            }

            .error {
                color: red;
            }
        </style>
    </head>
    <body onload="document.login.username.focus()">
        <h1>Login</h1>
        <c:if test="${authFailed}">
            <p class="error">The email address or password you entered was incorrect.</p>
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
        </form>
    </body>
</html>
