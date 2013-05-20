<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="application/json" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

[
<c:forEach var="entry" items="${sarariman.locationLog.latest}" varStatus="loopStatus">
    {
    "employeeDisplayName": "${entry.key.displayName}",
    "latitude": ${entry.value.coords.latitude},
    "longitude": ${entry.value.coords.longitude},
    "accuracy": ${entry.value.coords.accuracy},
    "timestamp": ${entry.value.timestamp},
    "icon": "${entry.key.photoURL}"
    }
    <c:if test="${!loopStatus.last}">,</c:if>
</c:forEach>
]
