<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../css/style.css" rel="stylesheet" media="screen"/>
        <style type="text/css">
            #map-canvas {
                display: block;
                height: 400px;
            }

            /* Keep the bootstrap stuff from interfering with Google Maps stuff.
               See https://github.com/twitter/bootstrap/issues/2410#issuecomment-4342173 */
            #map-canvas img {
                max-width: none;
            }
        </style>

        <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript">
            function formatDate(date, fmt) {
                function pad(value) {
                    return (value.toString().length < 2) ? '0' + value : value;
                }
                return fmt.replace(/%([a-zA-Z])/g, function (_, fmtCode) {
                    switch (fmtCode) {
                        case 'Y':
                            return date.getUTCFullYear();
                        case 'M':
                            return pad(date.getUTCMonth() + 1);
                        case 'd':
                            return pad(date.getUTCDate());
                        case 'H':
                            return pad(date.getUTCHours());
                        case 'm':
                            return pad(date.getUTCMinutes());
                        case 's':
                            return pad(date.getUTCSeconds());
                        default:
                            throw new Error('Unsupported format code: ' + fmtCode);
                    }
                });
            }

            function initialize() {
                var StackFrameWorldHeadquarters = new google.maps.LatLng(28.758822, -81.294179);
                var mapOptions = {
                    center: StackFrameWorldHeadquarters,
                    zoom: 12,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };

                var map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
                var bounds = new google.maps.LatLngBounds();

                $.getJSON("latest.jsp", function(entries) {
                    var infowindow = new google.maps.InfoWindow();

                    $.each(entries, function(key, entry) {
                        var latLng = new google.maps.LatLng(entry.latitude, entry.longitude);
                        bounds.extend(latLng);
                        var image = {
                            url: entry.icon,
                            scaledSize: new google.maps.Size(25, 25)
                        }

                        var marker = new google.maps.Marker({
                            position: latLng,
                            title: entry.employeeDisplayName,
                            icon: image
                        });

                        google.maps.event.addListener(marker, 'click', function() {
                            var content = "<p>" + entry.employeeDisplayName + "<br/>" + "Last seen: " +
                                formatDate(new Date(entry.timestamp), '%M-%d %H:%m:%s') + "</p>";
                            infowindow.setContent(content);
                            infowindow.open(map, marker);
                        });
                        marker.setMap(map);
                    });
                });
                map.fitBounds(bounds);
            }
            google.maps.event.addDomListener(window, 'load', initialize);
        </script>
        <title>Latest Employee Locations</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">

            <h1>Latest Employee Locations</h1>

            <div id="map-canvas" class="span12">
            </div>

            <%@include file="../footer.jsp" %>
        </div>

    </body>
</html>