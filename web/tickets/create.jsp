<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

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
        <title>Create a Ticket</title>

        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script>
            function gotCurrentPosition(position) {
                $("input[name='has_creator_location']").val("1");
                $("input[name='latitude']").val(position.coords.latitude);
                $("input[name='longitude']").val(position.coords.longitude);
            }
            
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(gotCurrentPosition);
            } 
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>
            <h1>Create a Ticket</h1>

            <form method="POST" action="handleCreate">
                <p>
                    <label for="name">Name: </label>
                    <input size="50" type="text" id="name" name="name"/><br/>
                    <input type="hidden" id="has_creator_location" name="has_creator_location" value="0"/>
                    <input type="hidden" id="latitude" name="latitude" value="0.0"/>
                    <input type="hidden" id="longitude" name="longitude" value="0.0"/>
                    <input type="submit" value="Create" name="create"/>
                </p>
                <p>Enter a name for the ticket.</p>
            </form>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
