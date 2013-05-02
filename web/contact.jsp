<%--
  Copyright (C) 2010-2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<c:if test="${!user.administrator}">
    <jsp:forward page="unauthorized"/>
</c:if>

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
        <title>Contact</title>
        <style type="text/css">
            /*
                label {
                    float: left;
                    width: 5em;
                    margin-right: 1em;
                    text-align: left;
                }

                legend {
                    margin-left: 1em;
                    padding-top: 1em;
                    color: #000;
                    font-weight: bold;
                }

                fieldset {
                    float: left;
                    clear: both;
                    width: 100%;
                    margin: 0 0 -1em 0;
                    padding: 0 0 1em 0;
                    border-style: none;
                }

                ol {
                    padding-top: 0.25em;
                    list-style: none;
                }

                li {
                    float: left;
                    clear: left;
                    width: 100%;
                    padding-bottom: 0.25em;
                }

                input.submit {
                    float: none;
                    width: auto;
                }
            */
        </style>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container-fluid">

            <c:if test="${param.action != 'create'}">
                <sql:query dataSource="jdbc/sarariman" var="result">
                    SELECT * FROM contacts WHERE id = ?
                    <sql:param value="${param.id}"></sql:param>
                </sql:query>
                <c:set var="contact" value="${result.rows[0]}"/>
            </c:if>

            <form method="POST" action="ContactController" class="form-horizontal">
                <div class="control-group">
                    <label class="control-label" for="name">Name</label>
                    <div class="controls">
                        <input type="text" id="name" name="name" value="${contact.name}"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="title">Title</label>
                    <div class="controls">
                        <input type="text" id="title" name="title" value="${contact.title}"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="email">Email</label>
                    <div class="controls">
                        <input type="text" id="email" name="email" size="30" value="${contact.email}"/>
                    </div>
                </div>

                <fieldset>
                    <legend>Phone Numbers</legend>

                    <div class="control-group">
                        <label class="control-label" for="phone">Phone</label>
                        <div class="controls">
                            <input type="text" id="phone" name="phone" value="${contact.phone}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="fax">Fax</label>
                        <div class="controls">
                            <input type="text" id="fax" name="fax" value="${contact.fax}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="mobile">Mobile</label>
                        <div class="controls">
                            <input type="text" id="mobile" name="mobile" value="${contact.mobile}"/>
                        </div>
                    </div>
                </fieldset>

                <fieldset>
                    <legend>Address</legend>
                    <div class="control-group">
                        <label class="control-label" for="mobile">Street</label>
                        <div class="controls">
                            <input type="text" id="street" name="street" value="${contact.street}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="mobile">City</label>
                        <div class="controls">
                            <input type="text" id="city" name="city" value="${contact.city}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="mobile">State</label>
                        <div class="controls">
                            <input type="text" id="state" name="state" size="2" value="${contact.state}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="mobile">ZIP</label>
                        <div class="controls">
                            <input type="text" id="zip" name="zip" size="10" value="${contact.zip}"/>
                        </div>
                    </div>
                </fieldset>

                <c:choose>
                    <c:when test="${param.action == 'create'}">
                        <input class="submit" type="submit" value="Create" <c:if test="${!user.administrator}">disabled="true"</c:if>/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="id" value="${param.id}"/>
                        <input class="btn" type="submit" value="Update" <c:if test="${!user.administrator}">disabled="true"</c:if>/>
                    </c:otherwise>
                </c:choose>

                <input type="hidden" name="action" value="${param.action}"/>
            </form>

            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>
