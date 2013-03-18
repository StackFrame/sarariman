<%--
  Copyright (C) 2009-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<div id="topnav">
    <p>
        <a class="icon-home" title="Go to your home page" href="${pageContext.request.contextPath}/"></a> <a href="${pageContext.request.contextPath}/tools">Tools</a>
        <span style="float: right">
            <a href="${user.URL}">${user.userName}</a>
            <a href="${user.URL}"><img width="25" height="25" onerror="this.style.display='none'" src="${user.photoURL}"/></a>
        </span>
    </p>
</div>
