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
        <title>Schedule An Event</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />
        <script type="text/javascript" src="../jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script type="text/javascript" src="../js/jquery-ui-timepicker-addon.js"></script>
        <link href="../style/timepicker.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript">
            $(function() {
                $( "#begin" ).datetimepicker({dateFormat: 'yy-mm-dd'});
                $( "#end" ).datetimepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>
            <h1>Schedule An Event</h1>

            <!-- FIXME: Validate that end is greater than begin. -->

            <form method="POST" action="handleCreate.jsp">
                <p>
                    <label for="begin">Begin: </label>
                    <input size="17" type="text" id="begin" name="begin"/>

                    <label for="end">End: </label>
                    <input size="17" type="text" id="end" name="end"/><br/>

                    <label for="name">Name: </label>
                    <input size="50" type="text" id="name" name="name"/><br/>

                    <label for="location">Location: </label>
                    <input size="50" type="text" id="location" name="location"/><br/>

                    <label for="location_url">Location URL: </label>
                    <input size="50" type="text" id="location_url" name="location_url"/><br/>

                    <label for="location_map_url">Location Map URL: </label>
                    <input size="50" type="text" id="location_map_url" name="location_map_url"/><br/>

                    <label for="description">Description: </label>
                    <input size="50" type="text" id="description" name="description"/><br/>

                    <input type="submit" value="Schedule" name="schedule"/>
                </p>
                <p>
                    Enter the begin and end time for the event. Supplying a location and description is optional.
                </p>
            </form>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
