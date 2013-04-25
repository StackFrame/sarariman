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
        <title>Sarariman Sign In</title>
        <style>
            h1.title {
                text-align: center;
            }
        </style>
    </head>
    <body onload="document.login.username.focus()">
        <div class="container-fluid">
            <h1 class="title">Sarariman Sign In</h1>
            <form class="form-horizontal" id="login" name="login" method="POST" action="auth_check">
                <c:if test="${authFailed}">
                    <p class="text-error">The email address or password you entered was incorrect.</p>
                </c:if>

                <div class="control-group">
                    <label class="control-label" for="username">Email</label>
                    <div class="controls">
                        <input type="text" id="username" name="username" placeholder="Email">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password">Password</label>
                    <div class="controls">
                        <input type="password" id="password" name="password" placeholder="Password">
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <label class="checkbox">
                            <input type="checkbox" name="remember"> Remember me
                        </label>
                        <button type="submit" class="btn">Sign In</button>
                    </div>
                </div>
                <input type="hidden" name="destination" value="${param.destination}"/>
            </form>
        </div>
    </body>
</html>
