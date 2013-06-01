<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../css/style.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script src="../js/bootstrap.js"></script>
        <title>${conference.name}</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>${conference.name}</h1>

            <p>
            To join this conference in your XMPP/Jabber client, go to
            <a href="xmpp:${conference.name}@conference.stackframe.com?join">${conference.name}@conference.stackframe.com</a>.
            </p>

            <h2>Occupants</h2>
            <ul>
                <c:forEach var="occupant" items="${conference.chatRoom.occupants}">
                    <!-- FIXME: What happens if an entity is joined multiple times to the MUC with different resources? -->
                    <li>${occupant.entity.bareJID}</li>
                </c:forEach>
            </ul>

            <%@include file="/footer.jsp" %>
        </div>
    </body>
</html>
