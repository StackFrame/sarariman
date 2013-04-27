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
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Sarariman Sign In</title>
        <style>
            h1.title {
                text-align: center;
            }

            .center {
                float: none;
                margin-left: auto;
                margin-right: auto;
            }

            .message {
                text-align: center;
            }


            body {
                padding-top: 40px;
                padding-bottom: 40px;
                background-color: #f5f5f5;
            }

            .form-signin {
                max-width: 300px;
                padding: 19px 29px 29px;
                margin: 0 auto 20px;
                background-color: #fff;
                border: 1px solid #e5e5e5;
                border-radius: 5px;
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
            }

            .form-signin .form-signin-heading,
            .form-signin .checkbox {
                margin-bottom: 10px;
            }

        </style>
    </head>
    <body onload="document.login.username.focus()">
        <div class="span7 center">
            <form class="form-signin" id="login" name="login" method="POST" action="auth_check">
                <h1 class="form-signin-heading">Sign In</h1>

                <c:if test="${authFailed}">
                    <p class="text-error message">The email address or password you entered was incorrect.</p>
                </c:if>

                <div class="input-prepend">
                    <span class="add-on"><i class="icon-envelope"></i></span>
                    <input type="email" id="username" name="username" placeholder="Email">
                </div>
                <div class="input-prepend">
                    <span class="add-on"><i class="icon-key"></i></span>
                    <input type="password" id="password" name="password" placeholder="Password">
                </div>
                <label class="checkbox">
                    <input type="checkbox" name="remember"> Remember me
                </label>
                <button type="submit" class="btn">Sign In</button>
                <input type="hidden" name="destination" value="${param.destination}"/>
            </form>
        </div>
    </body>
</html>
