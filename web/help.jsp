<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="css/style.css" rel="stylesheet" media="screen"/>

        <script type="text/javascript" src="jquery/js/jquery-1.7.2.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Sarariman Help</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>Help</h1>

            <h2>Introduction</h2>

            <p>
                Sarariman is our business management system. Specifically, it addresses our need for accountability of our billable
                time.
            </p>

            <h2>Recording Your Time</h2>

            <p>
                You record your time right on the home page. Don't take time recording lightly. Only record time once you have
                worked it. You are able to modify a time entry once you have recorded it, but this should only need to be done
                occasionally. An audit trail is kept of all modifications to time entries.
            </p>

            <h2>Submitting Your Timesheet</h2>

            <p>
                Once you have recorded your time for the week, you submit your timesheet.  Once a timesheet has been submitted,
                entries cannot be added or modified.  A submitted timesheet can be approved or rejected.  If a timesheet is
                rejected, modifications can be made before resubmission.
            </p>

            <p>
                If you need to change a task for a time entry, modify the time entry to a duration of 0 hours and then add a new
                entry.
            </p>

            <h2>Bugs/Enhancements</h2>
            <p>
                If you find a bug or have an idea for an enhancement, <a href="/tickets/">create a ticket</a>.
            </p>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
