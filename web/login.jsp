<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
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
    <body>
        <h1>Login</h1>
        <c:if test="${authFailed}">
            <p class="error">The email address or password you entered was incorrect.</p>
        </c:if>

        <form name="login" method="POST" action="auth_check">
            <p>
                <strong>email address: </strong>
                <input type="text" name="username" size="40"/>
            </p>
            <p>
                <strong>password: </strong>
                <input type="password" size="25" name="password"/>
            </p>
            <p>
                <input type="submit" value="Sign In"/>
            </p>
        </form>
    </body>
</html>
