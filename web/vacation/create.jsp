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
        <title>Schedule Vacation Time</title>

        <!-- jQuery -->
        <link type="text/css" href="../jquery/css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />
        <script type="text/javascript" src="../jquery/js/jquery-ui-1.8.20.custom.min.js"></script>
        <!-- /jQuery -->

        <script>
            $(function() {
                $( "#begin" ).datepicker({dateFormat: 'yy-mm-dd'});
                $( "#end" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    </head>
    <body>
        <div class="container">
            <%@include file="/WEB-INF/jspf/userMenu.jspf" %>
            <h1>Schedule Vacation Time</h1>

            <!-- FIXME: Validate that end is greater than begin. -->

            <form method="POST" action="handleCreate.jsp">
                <p>
                    <label for="begin">Begin: </label>
                    <input size="10" type="text" id="begin" name="begin"/>

                    <label for="end">End: </label>
                    <input size="10" type="text" id="end" name="end"/><br/>

                    <label for="comment">Comment: </label>
                    <input size="50" type="text" id="comment" name="comment"/><br/>

                    <input type="submit" value="Schedule" name="schedule"/>
                </p>
                <p>
                    Enter the begin and end date of your vacation time. If you are going to be out for a single day, just set the end
                    date to be the same as the begin date. Supplying a comment is optional.
                </p>
            </form>

            <%@include file="../footer.jsp" %>
        </div>
    </body>
</html>
