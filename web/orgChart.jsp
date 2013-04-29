<%--
  Copyright (C) 2012-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page import="java.util.Collection"%>
<%@page import="com.stackframe.sarariman.Sarariman"%>
<%@page import="com.stackframe.sarariman.Employee"%>
<%@page import="com.stackframe.sarariman.OrganizationHierarchy"%>
<%@page import="com.stackframe.sarariman.Directory"%>
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
        <title>Org Chart</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>
        <div class="container-fluid">
            <h1>Org Chart</h1>

            <%!
                String write(OrganizationHierarchy.Node node) {
                    StringBuilder buf = new StringBuilder();
                    Directory directory = (Directory)getServletContext().getAttribute("directory");
                    Employee employee = directory.getByNumber().get(node.id());
                    String name = employee.getFullName();
                    buf.append(String.format("<img class=\"img-rounded\" src=\"%s\" width=\"25\" height=\"25\" onerror=\"this.style.display='none'\"/>", employee.getPhotoURL()));
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
        </div>
    </body>
</html>
