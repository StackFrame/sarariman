<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="../style.css" rel="stylesheet" type="text/css"/>
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
        <%@include file="../header.jsp" %>
        <h1>Create a Ticket</h1>

        <form method="POST" action="handleCreate.jsp">
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
    </body>
</html>
