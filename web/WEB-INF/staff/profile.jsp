<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sarariman" uri="/WEB-INF/tlds/sarariman" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

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
        <title>${employee.displayName}</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">
            <h1>${employee.displayName}</h1>

            <a href="${employee.photoURL}" title="view full size profile photo">
                <img class="img-rounded" id="photo" width="100" height="100" onerror="this.style.display='none'"
                     src="${employee.photoURL}" alt="profile photo of ${employee.displayName}"/>
            </a>

            <p>
                <c:forEach var="title" items="${employee.titles}">
                    ${title}<br/>
                </c:forEach>
            </p>

            <p>
                <a href="mailto:${employee.email}" title="email ${employee.displayName}">
                    <i class="icon-envelope"></i> ${employee.email}
                </a><br/>

                surname: ${employee.surname}<br/>
                givenName: ${employee.givenName}<br/>

                <a href="xmpp:${employee.email}" title="chat with ${employee.displayName}">
                    <i class="icon-comment"></i> ${employee.email}
                </a><br/>
                <!-- FIXME: Add presence. -->
                <!-- FIXME: Maybe add last location check in. -->


                <!-- FIXME: This should take the locale of the user into account. -->
                <a href="${mobileNumberURI}" title="call ${employee.displayName}">
                    <i class="icon-mobile-phone"></i> ${formattedMobileNumber}
                </a><br/>

                <c:forEach var="profile" items="${profiles}">
                    <a href="${profile.URL}"><i class="${profile.iconClass}"></i> ${profile.anchorText}</a><br/>
                </c:forEach>

                <i class="icon-calendar"></i> Birthday: <joda:format value="${employee.birthdate}" pattern="MMMM d" /><br/>
                <a href="${employee.userName}?type=text/vcard" title="download as a vCard"><i class="icon-download"></i> vCard</a>
                <a href="${employee.userName}?type=application/vcard%2Bxml" title="download as an xCard"><i class="icon-download"></i> xCard</a>
            </p>

            <%@include file="/footer.jsp" %>
        </div>
    </body>
</html>
