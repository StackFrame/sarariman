<%--
  Copyright (C) 2012 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Collection,com.stackframe.sarariman.Sarariman,com.stackframe.sarariman.Employee,com.stackframe.sarariman.OrganizationHierarchy,com.stackframe.sarariman.Directory"%>
<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="style.css" rel="stylesheet" type="text/css"/>
        <title>Org Chart</title>
    </head>
    <body>
        <%@include file="header.jsp" %>

        <h1>Org Chart</h1>

        <%!
            String write(OrganizationHierarchy.Node node) {
                StringBuilder buf = new StringBuilder();
                Directory directory = (Directory)getServletContext().getAttribute("directory");
                String name = directory.getByNumber().get(node.id()).getFullName();
                buf.append("<a href=\"employee?id=" + node.id() + "\">");
                buf.append(name);
                buf.append("</a>");
                buf.append('\n');
                Collection<OrganizationHierarchy.Node> directReports = node.directReports();
                if (!directReports.isEmpty()) {
                    buf.append(write(directReports));
                    buf.append('\n');
                }

                return buf.toString();
            }

            String write(Collection<OrganizationHierarchy.Node> nodes) {
                StringBuilder buf = new StringBuilder();
                buf.append("<ul>");
                for (OrganizationHierarchy.Node node : nodes) {
                    buf.append("<li>");
                    buf.append(write(node));
                    buf.append("</li>\n");
                }

                buf.append("</ul>");
                buf.append('\n');

                return buf.toString();
            }
        %>

        <%
            Sarariman sarariman = (Sarariman)getServletContext().getAttribute("sarariman");
            Collection<OrganizationHierarchy.Node> orgChart = sarariman.getOrganizationHierarchy().getOrgChart();
            out.print(write(orgChart));
        %>

        <%@include file="footer.jsp" %>
    </body>
</html>
